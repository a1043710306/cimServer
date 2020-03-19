package com.luz.route.cache;


import com.luz.route.constant.Constant;
import com.luz.route.redis.JedisClusterConfig;
import com.luz.route.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;

/**
 * 离线消息 存储服务
 */
public class OfflinePushDB {
    private static Logger LOGGER= LoggerFactory.getLogger(OfflinePushDB.class);
    private static JedisCluster jedisCluster;
    private static Jedis jedis;
    static {
        jedisCluster= SpringUtils.getObject(JedisClusterConfig.class).getJedisCluster();
        //jedis=SpringUtils.getObject(JedisClusterConfig.class).getDefRedis();
    }

    /**
     * 存储离线用户的消息
     * @param userId
     * @param msg
     */
    public static void saveOfflineMSG(int userId,String msg){
        LOGGER.debug("save offline msg to redis:{"+userId+"}");
        String key= Constant.OFFLINE_MSG_INDEX+userId;
        jedisCluster.lpush(key,msg);
        jedisCluster.expire(key,Constant.OFFLINE_MSG_ATTR_DAY*30);

    }

    /**
     * 检查用户是否含有离线消息
     * @param userId
     * @return
     */
    public static boolean checkOfflineMsg(int userId){
        String key=Constant.OFFLINE_MSG_INDEX+userId;
        return jedisCluster.exists(key);
    }

    /**
     * 获取离线消息
     * @param userId
     * @return
     */
    public static List<String> getOfflineMSG(int userId){
        String key=Constant.OFFLINE_MSG_INDEX+userId;
        List<String> offlineMessages=new ArrayList<>();
        String message=jedisCluster.lpop(key);
        //String message=jedis.lpop(key);
        while(message!=null){
            offlineMessages.add(message);
            message=jedisCluster.lpop(key);
            //message=jedis.lpop(key);
        }
        LOGGER.debug("get offline msg to redis:{"+userId+"}  msg count "+offlineMessages.size());
        //删除队列
        jedisCluster.del(key);
        //jedis.del(key);
        return offlineMessages;
    }

    /**
     * 保存push消息
     * @param userId
     * @param msg
     */
    public static void saveOfflinePushMSG(int userId,String msg){
        LOGGER.debug("save offline msg to redis:{"+userId+"}");
        String key= Constant.OFFLINE_PUSH_MSG_INDEX+userId;
        jedisCluster.lpush(key,msg);
        jedisCluster.expire(key,Constant.OFFLINE_MSG_ATTR_DAY*30);
    }

    /**
     * 检查用户是否含有未推送的push消息
     * @param userId
     * @return
     */
    public static boolean checkOfflinePushMsg(int userId){
        String key=Constant.OFFLINE_PUSH_MSG_INDEX+userId;
        return jedisCluster.exists(key);
    }

    /**
     * 获取push消息
     * @param userId
     * @return
     */
    public static List<String> getOfflinePushMSG(int userId){
        String key=Constant.OFFLINE_PUSH_MSG_INDEX+userId;
        List<String> offlineMessages=new ArrayList<>();
        String message=jedisCluster.lpop(key);
        //String message=jedis.lpop(key);
        while(message!=null){
            offlineMessages.add(message);
            message=jedisCluster.lpop(key);
            //message=jedis.lpop(key);
        }
        LOGGER.debug("get offline msg to redis:{"+userId+"}  msg count "+offlineMessages.size());
        //删除队列
        jedisCluster.del(key);
        //jedis.del(key);
        return offlineMessages;
    }
}
