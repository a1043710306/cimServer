package com.luz.hormone.zookeeper;

import com.luz.hormone.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegZk  implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(RegZk.class);

    private ZkServicer zkServicer;

    private String root;

    private String ip;
    private int cimServerPort;
    private int httpPort;

    public RegZk(String ip, int cimServerPort,int httpPort) {
        this.ip = ip;
        this.cimServerPort = cimServerPort;
        this.httpPort = httpPort;
        zkServicer = SpringUtils.getObject(ZkServicer.class);
        zkServicer.init();
        root = zkServicer.getRoot();
    }
    @Override
    public void run() {
        zkServicer.createRootNode();
        if (zkServicer.getIsZk()){
            String path = root + "/ip-" + ip + ":" + cimServerPort + ":" + httpPort;
            if (zkServicer.checkNode(path)){
                zkServicer.delNode(path);
            }
            zkServicer.createNode(path);
            logger.info("注册 zookeeper 成功，msg=[{}]", path);
        }
    }
}
