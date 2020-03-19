package com.luz.route.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.luz.route.cache.OfflinePushDB;
import com.luz.route.constant.Constant;
import com.luz.route.enetity.DataPackage;
import com.luz.route.service.OfflinePushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfflinePushServiceImpl implements OfflinePushService {
    Logger LOGGER= LoggerFactory.getLogger(OfflinePushServiceImpl.class);



    @Override
    public void saveOfflineMSG(int userId, String msg) {
        OfflinePushDB.saveOfflineMSG(userId,msg);
    }

    @Override
    public void saveOfflinePushMSG(int userId, DataPackage dataPackage) {

    }
}
