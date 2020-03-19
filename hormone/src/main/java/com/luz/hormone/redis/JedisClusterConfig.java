package com.luz.hormone.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author:
 * @Description:
 * @Date:
 */
@Configuration
@Service
public class JedisClusterConfig {
    @Autowired
    private RedisProperties redisProperties;
    @Value("${defaultRedis}")
    String defRedisHost;

    private Jedis jedis;

    public JedisCluster getJedisCluster(){
        String [] serverArray=redisProperties.getClusterNodes().split(",");
        Set<HostAndPort> nodes=new HashSet<>();
        for (String ipPort:serverArray){
            String [] ipPortPair=ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(),Integer.valueOf(ipPortPair[1].trim())));

        }
        return  new JedisCluster(nodes,redisProperties.getCommandTimeout());
    }

    //本地测试方法
    public Jedis getDefRedis(){
        String ip=defRedisHost.split(":")[0];
        int port=Integer.valueOf(defRedisHost.split(":")[1]);
        if (jedis==null){
            jedis=new Jedis(ip,port);
        }
        return jedis;
    }

}
