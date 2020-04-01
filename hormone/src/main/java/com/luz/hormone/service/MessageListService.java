package com.luz.hormone.service;

import com.luz.hormone.dataPackage.DataPackage;
import com.luz.hormone.entity.MessageListEntity;

public interface MessageListService {
    public DataPackage process(DataPackage dataPackage);
    public DataPackage processWebMessage(DataPackage dataPackage);
    public String sendMessage(MessageListEntity messageListEntity);
    public String getHistory(MessageListEntity messageListEntity);
}
