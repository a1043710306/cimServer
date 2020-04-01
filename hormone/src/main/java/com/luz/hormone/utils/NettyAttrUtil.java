package com.luz.hormone.utils;

import com.luz.hormone.redis.JedisClusterConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class NettyAttrUtil {
    private  static final String ATTR_KEY_HEART="CHANNEL-";
    private  static JedisCluster jedisCluster;
    private  static Jedis jedis;

    /**
     * 注入 redis辅助工具
     */
    static {
        JedisClusterConfig jedisClusterConfig=SpringUtils.getObject(JedisClusterConfig.class);
        jedisCluster=jedisClusterConfig.getJedisCluster();
    }

    /**
     * 设置心跳
     * @param channel
     * @param time
     * @return
     */
    public static boolean saveHeart(ChannelHandlerContext channel, int time){
        String key=ATTR_KEY_HEART+channel.channel().id();
        jedisCluster.set(key,channel.channel().remoteAddress().toString());
        //jedis.set(key,channel.channel().remoteAddress().toString());
        jedisCluster.expire(key,time);
        //jedis.expire(key,time);
        return true;
    }

    /**
     * 检查心跳
     * @param channel
     * @return
     */
    public static boolean checkHeart(ChannelHandlerContext channel){
        String key=ATTR_KEY_HEART+channel.channel().id();
        if (jedisCluster.exists(key)){
            return true;
        }
        return false;
    }

}
