package com.luz.route.service;

import com.luz.route.enetity.DataPackage;

public interface OfflinePushService {
    public void saveOfflineMSG(int userId, String msg);
    public void saveOfflinePushMSG(int userId, DataPackage dataPackage);
}
