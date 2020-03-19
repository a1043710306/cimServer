package com.luz.route.utils;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class IpMapUtils {
    private static Map<String,String> IP_MAP=new ConcurrentHashMap<>();
    private static ConfigUtils configUtils;
    static {
        configUtils=SpringUtils.getObject(ConfigUtils.class);
        String ip=configUtils.getIp();
        for (String i:ip.split(",")){
            IP_MAP.put(i.split(":")[0],i.split(":")[1]);
        }
    }
    public static String getIptpInetIp(String ip){
        return IP_MAP.get(ip);
    }
}
