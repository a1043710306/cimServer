package com.luz.hormone.service;

import com.luz.hormone.dataPackage.DataPackage;

public interface OfflinePushService {
    public void process(int userId);
    public void saveOfflineMSG(int userId,String msg);
    public void saveOfflinePushMSG(int userId, DataPackage dataPackage);
}
