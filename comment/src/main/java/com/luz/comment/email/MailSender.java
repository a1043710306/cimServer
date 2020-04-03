package com.luz.comment.email;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;


/**
 * Created by Genix on 2016/04/16.
 */

public class MailSender
{
	private MimeMessage msg;
	private Multipart mp;
	private int seqNo = 0;

	public MailSender(String smtpHost, int smtpPort, boolean fSSLConnection) throws MessagingException
	{
		this(smtpHost, smtpPort, (String)null, (String)null, fSSLConnection);
	}
	public MailSender(String smtpHost, int smtpPort, String userName, String password, boolean fSSLConnection) throws MessagingException
	{

		SmtpAuthenticator auth = null;
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		if (fSSLConnection)
		{
			props.put("mail.smtp.socketFactory.port", smtpPort);
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		if (userName!=null && userName.length()>0 && password!=null && password.length()>0)
		{
			props.put("mail.smtp.auth", "true");
			auth = new SmtpAuthenticator(userName, password);
		}
		// Get a Session object
		Session session = Session.getDefaultInstance(props, auth);
		// construct the message
		msg = new MimeMessage(session);
		mp = new MimeMultipart("related");
	}

	public MailSender(){

	}
	public void setSubject(String subject) throws AddressException, MessagingException
	{
		msg.setSubject(subject);
	}

	public void setMailTo(String to)
	{
		try{
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		}catch (AddressException e){
			e.printStackTrace();
		}catch (MessagingException e){
			e.printStackTrace();
		}
	}

	public void setMailTo(String[] to) throws AddressException, MessagingException
	{
		msg.setRecipients(Message.RecipientType.TO, getInternetAddress(to));
	}

	public void setMailFrom(String from) throws AddressException, MessagingException
	{
		msg.setFrom(new InternetAddress(from));
	}

	public void setMailFrom(String from, String alias) throws AddressException, MessagingException, UnsupportedEncodingException
	{
		msg.setFrom(new InternetAddress(from, alias));
	}

	public void setMailCc(String[] cc) throws AddressException, MessagingException
	{
		msg.setRecipients(Message.RecipientType.CC, getInternetAddress(cc));
	}

	public void setMailBcc(String[] bcc) throws AddressException, MessagingException
	{
		msg.setRecipients(Message.RecipientType.BCC, getInternetAddress(bcc));
	}

	public synchronized String genContentId()
	{
		seqNo++;
		return new String("id_"+seqNo);
	}
	public String[] genContentIdArray(int size)
	{
		ArrayList list = new ArrayList();
		for (int i=0; i<size; i++)
			list.add(genContentId());
		return (String[])list.toArray(new String[0]);
	}
	public String setContentFile(Object file) throws MessagingException
	{
		return setContentFile(file, genContentId());
	}
	public String setContentFile(Object file, String contentId) throws MessagingException
	{
		try
		{
			MimeBodyPart mbp = new MimeBodyPart();
			DataSource ds = getDataSource(file);
			if (ds==null)
				return null;
			mbp.setDataHandler(new DataHandler(ds));
			mbp.setHeader("Content-ID", contentId);
			mbp.setFileName(contentId);
			mp.addBodyPart(mbp);
			return contentId;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public void setHtmlContentByTemplate(String content, Object[] value) throws MessagingException, IOException
	{
		setContentByTemplate(content, value, "text/html");
	}
	public void setContentByTemplate(String content, Object[] value) throws MessagingException, IOException
	{
		setContentByTemplate(content, value, "text/plain");
	}
	public void setContentByTemplate(String content, Object[] value, String mimeType) throws MessagingException, IOException
	{
		content = MessageFormat.format(content, value);
		setContent(content, mimeType);
	}
	public void setContent(String content, String mimeType) throws MessagingException, IOException
	{
		MimeBodyPart mbp = new MimeBodyPart();
		mbp.setText(content);
		mbp.setDataHandler(new DataHandler(new ByteArrayDataSource(content, mimeType)));
		mp.addBodyPart(mbp);
	}
	public void setContent(String content)
	{
		try{
			setContent(content, "text/plain");
		}catch (MessagingException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	public void setHtmlContent(String content) throws MessagingException, IOException
	{
		setContent(content, "text/html");
	}

	public void setAttachedFile(Object[] attachedFile) throws MessagingException, UnsupportedEncodingException
	{
		for (int i=0; i<attachedFile.length; i++)
		{
			MimeBodyPart mbp = new MimeBodyPart();
			DataSource ds = getDataSource(attachedFile[i]);
			if (ds==null)
				continue;
			mbp.setDataHandler(new DataHandler(ds));
			mbp.setFileName(MimeUtility.encodeText(ds.getName(), System.getProperty("file.encoding"), null));
			mp.addBodyPart(mbp);
		}
	}

	public void send() throws MessagingException
	{
		msg.setContent(mp);
		msg.setSentDate(new Date());
		Transport.send(msg);
	}

	protected InternetAddress[] getInternetAddress(String[] address) throws AddressException
	{
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<address.length; i++)
		{
			if (i!=0)
				sb.append(",");
			sb.append(address[i]);
		}
		return InternetAddress.parse(sb.toString());
	}
	protected boolean exists(URL url)
	{
		try
		{
			InputStream is = url.openStream();
			int size = is.read();
			is.close();
			return size>0;
		}
		catch (Exception e)
		{
			//e.printStackTrace();
		}
		return false;
	}
	protected DataSource getDataSource(Object src)
	{

		DataSource ds = null;
		if (src instanceof URL)
		{
			if (!exists((URL)src))
				return null;
			ds = new URLDataSource((URL)src);
		}
		else
			ds = new FileDataSource((String)src);
		return ds;
	}



}
