package com.luz.hormone.utils;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelUtil {
    private static final Map<ChannelHandlerContext,Integer> CHANNEL_USER_MAP= new ConcurrentHashMap<>();
    //冗余一份数据 方便操作
    private static final Map<Integer,ChannelHandlerContext> USER_CHANNEL_MAP= new ConcurrentHashMap<>();

    public static void put(int userId,ChannelHandlerContext channel){
        CHANNEL_USER_MAP.put(channel,userId);
        USER_CHANNEL_MAP.put(userId,channel);
    }

    public static ChannelHandlerContext  getChannel(int userId){
        return USER_CHANNEL_MAP.get(userId)!=null?USER_CHANNEL_MAP.get(userId):null;
    }

    public static int getUserId(ChannelHandlerContext channel){
        return CHANNEL_USER_MAP.get(channel)!=null?CHANNEL_USER_MAP.get(channel):0;
    }

    public static boolean removeChannelInfo(int userId){
        ChannelHandlerContext channel=USER_CHANNEL_MAP.get(userId);
        if (channel!=null){
            USER_CHANNEL_MAP.remove(userId);
            CHANNEL_USER_MAP.remove(channel);
            return true;
        }
        return false;
    }
    public static boolean removeChannelInfo(ChannelHandlerContext channel){
        Integer userId=CHANNEL_USER_MAP.get(channel);
        if (userId!=null){
            USER_CHANNEL_MAP.remove(userId);
            CHANNEL_USER_MAP.remove(channel);
            return true;
        }
        return false;
    }

    public static Map<Integer,ChannelHandlerContext>getUsertoChannelMap(){
        return USER_CHANNEL_MAP;
    }
}
