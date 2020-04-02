package com.luz.hormone.dao;

import com.luz.hormone.constant.Constant;
import com.luz.hormone.redis.JedisClusterConfig;
import com.luz.hormone.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class ChatStatus {
    private static Logger LOGGER= LoggerFactory.getLogger(OfflinePushDB.class);
    private static JedisCluster jedisCluster;
    private static Jedis jedis;
    static {
        jedisCluster= SpringUtils.getObject(JedisClusterConfig.class).getJedisCluster();
    }

    /**
     * 保存聊天状态
     * @param userId
     * @param friendId
     */
    public static void saveStatus(int userId,int friendId){
        jedisCluster.set(Constant.USER_CHAT_STATUS_+userId,friendId+"");
    }

    /**
     * 是否有聊天状态
     * @param userId
     * @return
     */
    public static Boolean isStatus(int userId){
        return jedisCluster.exists(Constant.USER_CHAT_STATUS_+userId);
    }

    /**
     * 获取当前的聊天状态
     * @param userId
     * @return
     */
    public static int getStatus(int userId){
        String friendId=jedisCluster.get(Constant.USER_CHAT_STATUS_+userId);
        return friendId!=null?Integer.valueOf(friendId):0;
    }
}
