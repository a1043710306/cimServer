package com.luz.route.service;

import com.luz.route.enetity.DataPackage;
import com.luz.route.enetity.Node;

public interface SendMsg {
    public void send(Node node, int userId, String msg);
    public void sendCmdMsg(Node node,int userId,int cmd ,int code,String msg);
}
