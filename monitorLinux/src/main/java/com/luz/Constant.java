package com.luz;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Constant {
    public static String emailHost;
    public static Float CPU;
    public static Float RAM;
    public static Float DISK;
    public static Integer Refresh;
    public static Integer Vertex;
    public static String Sender;
    public static String Spasd;
    public static List<String> accept;

    static {
        try {
            InputStream in = Constant.class.getClassLoader().getResourceAsStream("common.properties");
            Properties p = new Properties();
            p.load(in);
            emailHost=p.getProperty("email.host");
            Sender=p.getProperty("email.sender");
            Spasd=p.getProperty("email.password");
            if (p.getProperty("email.accept")!=null){
                accept=new ArrayList<String>(Arrays.asList(p.getProperty("email.accept").split(",")));
            }else {
                accept=new ArrayList<String>(0);
            }
            CPU=p.getProperty("cpt.cpu")!=null?Float.valueOf(p.getProperty("cpt.cpu")):(float)0.8;
            RAM=p.getProperty("cpt.memory")!=null?Float.valueOf(p.getProperty("cpt.memory")):(float)0.9;
            DISK=p.getProperty("cpt.disk")!=null?Float.valueOf(p.getProperty("cpt.disk")):(float)0.99;
            Refresh=p.getProperty("cpt.Refresh")!=null?Integer.valueOf(p.getProperty("cpt.Refresh")):1;
            Vertex=p.getProperty("cpt.Vertex")!=null?Integer.valueOf(p.getProperty("cpt.Vertex")):10;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]){
        System.out.println(emailHost);
    }
}
