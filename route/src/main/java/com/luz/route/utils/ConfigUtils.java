package com.luz.route.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Configuration
@Component
public class ConfigUtils {
    @Value("${ip}")
    private String ip;
    @Value("${zk.root}")
    private String root;
    @Value("${zk.connection}")
    private String connection;
    @Value("${zk.iszk}")
    private boolean isZkSwitch;
    @Value("${defaultRedis}")
    private String defRedisHost;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public boolean isZkSwitch() {
        return isZkSwitch;
    }

    public void setZkSwitch(boolean zkSwitch) {
        isZkSwitch = zkSwitch;
    }

    public String getDefRedisHost() {
        return defRedisHost;
    }

    public void setDefRedisHost(String defRedisHost) {
        this.defRedisHost = defRedisHost;
    }
}
