package com.luz.hormone.service.impl;

import com.luz.hormone.cache.UserCache;
import com.luz.hormone.entity.Node;
import com.luz.hormone.service.UserConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service
public class UserConfigServiceImpl implements UserConfigService {
    Logger LOGGER= LoggerFactory.getLogger(UserConfigServiceImpl.class);
    @Autowired
    UserCache userCache;
    @Value("${mynetty.port}")
    int nettyPort;
    @Value("${server.port}")
    int serverPort;

    @Override
    public void saveRoute(int userId) {
        if (checkRoute(userId)){
            return ;
        }
        try{
            Node node=new Node();
            String ip=InetAddress.getLocalHost().getHostAddress();
            node.setIp(ip);
            node.setPort(nettyPort);
            node.setWebPort(serverPort);
            userCache.saveUserRoute(userId,node);
        }catch (Exception e){
            LOGGER.warn(e.toString());
            LOGGER.warn("User route store failed!");
        }

    }

    @Override
    public void delRoute(int userId) {
        if (checkRoute(userId)){
            userCache.delRoute(userId);
        }
    }

    @Override
    public boolean checkRoute(int userId) {
        return userCache.getUserRoute(userId)!=null;
    }
}
