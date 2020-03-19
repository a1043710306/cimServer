package com.luz.hormone.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.luz.hormone.dao.OfflinePushDB;
import com.luz.hormone.dataPackage.DataPackage;
import com.luz.hormone.netty.NettyServer;
import com.luz.hormone.service.OfflinePushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfflinePushServiceImpl implements OfflinePushService {
    Logger LOGGER= LoggerFactory.getLogger(OfflinePushServiceImpl.class);

    @Autowired
    private NettyServer nettyServer;

    public void pushOfflineMsg(int userId)  {
        LOGGER.info("start offline msg push");
        int pushCount=0;
        List<String> offlineMessage= OfflinePushDB.getOfflineMSG(userId);
        try{
            for (String msg:offlineMessage){
                nettyServer.sendMsg(userId,msg);
                pushCount++;
            }
        }catch (Exception e){
            LOGGER.warn(e.toString());
            LOGGER.warn("offline msg push is error,push interrupted");
        }
        LOGGER.info("offline msg count :"+pushCount+" fail count"+(offlineMessage.size()-pushCount));
        LOGGER.info("end offline msg push ");
    }

    public void pushOfflinePushMsg(int userId)  {
        LOGGER.info("start offline  push");
        int pushCount=0;
        List<String> offlineMessage= OfflinePushDB.getOfflinePushMSG(userId);
        try{
            for (String msg:offlineMessage){
                DataPackage dataPackage=JSONObject.parseObject(msg,DataPackage.class);
                nettyServer.sendMsg(userId,dataPackage);
                pushCount++;
            }
        }catch (Exception e){
            LOGGER.warn(e.toString());
            LOGGER.warn("offline msg push is error,push interrupted");
        }
        LOGGER.info("offline Push count :"+pushCount+" fail count"+(offlineMessage.size()-pushCount));
        LOGGER.info("end offline  push ");
    }

    @Override
    public void process(int userId) {
        if (OfflinePushDB.checkOfflineMsg(userId)){
            this.pushOfflineMsg(userId);
        }
        if (OfflinePushDB.checkOfflinePushMsg(userId)){
            this.pushOfflinePushMsg(userId);
        }
    }

    @Override
    public void saveOfflineMSG(int userId, String msg) {
        OfflinePushDB.saveOfflineMSG(userId,msg);
    }

    @Override
    public void saveOfflinePushMSG(int userId, DataPackage dataPackage) {
        String offlinePush= JSONObject.toJSONString(dataPackage);
        OfflinePushDB.saveOfflinePushMSG(userId,offlinePush);
    }
}
