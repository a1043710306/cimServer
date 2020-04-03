package com.luz.comment.utils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import com.sun.management.OperatingSystemMXBean;


public class ComputerInfoUtils {
    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final int MB=1024*1024;

    /**
     *
     * @return  返回cpu使用率
     */
    public static double getCpuLoad(){
        return osmxb.getSystemCpuLoad();
    }

    /**
     *   获取内存使用率
     * @return
     */
    public static double getMemoryLoad(){
        return 1-osmxb.getFreePhysicalMemorySize()*1.0/osmxb.getTotalPhysicalMemorySize();
    }

    public static Map<String,Integer> getDiskLoad(){
        Map<String,Integer> diskLoad=new HashMap<String, Integer>();
        File[] roots=File.listRoots();
        for (int i=0;i<roots.length;i++){
            File path=roots[i];
            String d1=path.getPath()!=null?path.getPath():"第"+(i+1)+"分区";
            int use=(int)((1.0*(path.getTotalSpace()/MB-path.getFreeSpace()/MB)/(path.getTotalSpace() / MB))*100);
            diskLoad.put(d1,use);
        }
        return diskLoad;
    }
    public static String getIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static void main(String args[])throws Exception{
        Map<String,Integer> disk=getDiskLoad();
        for (Map.Entry<String,Integer> entry:disk.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue()+"%");
        }
    }
}
