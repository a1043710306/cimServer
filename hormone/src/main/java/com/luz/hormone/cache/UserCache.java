package com.luz.hormone.cache;

import com.alibaba.fastjson.JSONObject;
import com.luz.hormone.constant.Constant;
import com.luz.hormone.entity.Node;
import com.luz.hormone.redis.JedisClusterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

@Service
public class UserCache {
    private Logger LOGGER= LoggerFactory.getLogger(UserCache.class);
    @Autowired
    JedisClusterConfig jedisClusterConfig;
    JedisCluster jedisCluster;
    private void init(){
        jedisCluster=jedisClusterConfig.getJedisCluster();
    }

    /**
     * 保存用户路由
     * @param userId
     * @param node
     */
    public void saveUserRoute(int userId, Node node){
        init();
        String nd= JSONObject.toJSONString(node);
        LOGGER.debug("save route "+userId+" :"+nd);
        String key= Constant.USER_ROUTE_INDEX+userId;
        jedisCluster.set(key,nd);
    }

    /**
     * 获取用户路由
     * @param userId
     * @return
     */
    public Node getUserRoute(int userId){
        init();
        String key= Constant.USER_ROUTE_INDEX+userId;
        String t=jedisCluster.get(key);
        Node n= JSONObject.parseObject(t,Node.class);
        LOGGER.debug("getRoute "+"userId: "+userId+" :"+t);
        return n;
    }

    /**
     * 删除用户路由
     * @param userId
     */
    public void delRoute(int userId){
        init();
        String key= Constant.USER_ROUTE_INDEX+userId;
        jedisCluster.del(key);
        LOGGER.debug("delRoute "+"userId: "+userId);
    }
}
