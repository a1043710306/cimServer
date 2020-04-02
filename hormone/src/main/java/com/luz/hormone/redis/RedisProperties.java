package com.luz.hormone.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author:
 * @Description:读取redis配置信息并装载
 * @Date: Create in  2020/3/31 16:16
 */
@Component
@ConfigurationProperties(prefix = "spring.redis.cache")
public class RedisProperties {
    private int expireSeconds;
    private String clusterNodes;
    private int commandTimeout;

    public int getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public int getCommandTimeout() {
        return commandTimeout;
    }

    public void setCommandTimeout(int commandTimeout) {
        this.commandTimeout = commandTimeout;
    }
}
