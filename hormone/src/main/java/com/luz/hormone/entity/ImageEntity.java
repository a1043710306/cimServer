package com.luz.hormone.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 2016年9月20日
 * sugang
 */
public class ImageEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7689648467324439055L;

	private String format;
	private Integer width;
	private Integer height;
	private String url;
	private String surl;
	private Integer fileSize;

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSurl() {
		return surl;
	}

	public void setSurl(String surl) {
		this.surl = surl;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
