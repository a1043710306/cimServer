package com.luz.route.zk;


import com.luz.route.utils.ConfigUtils;
import com.luz.route.utils.SpringUtils;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


public class ZkServicer {
    private static Logger LOGGER= LoggerFactory.getLogger(ZkServicer.class);

    private static String root;
    private static String connection;
    private static boolean isZkSwitch;

    private static  ZkClient zkClient;

    private static ConfigUtils configUtils;
    static{
        configUtils= SpringUtils.getObject(ConfigUtils.class);
        root=configUtils.getRoot();
        connection=configUtils.getConnection();
        isZkSwitch=configUtils.isZkSwitch();
        zkClient=new ZkClient(connection);
        LOGGER.info("zk is start");
    }

    /**
     * 创建父节点
     */
    public static void createRootNode(){
        boolean exists=zkClient.exists(root);
        if (exists){
            return ;
        }
        zkClient.createPersistent(root);
    }
    /**
     * 写入指定节点 临时目录
     *
     * @param path
     */
    public static void createNode(String path) {
        LOGGER.info("zk create node "+path);
        zkClient.createEphemeral(path);
    }

    /**
     * 删除节点
     * @param path
     */
    public static void delNode(String path){
        LOGGER.info("zk del node "+path);
        zkClient.delete(path);
    }

    /**
     * 获取所有节点
     * @return
     */
    public static List<String> getAllNode(String path){
        LOGGER.info("get node");
        return zkClient.getChildren(path);
    }

    /**
     * 检查节点
     * @param path
     */
    public static boolean checkNode(String path){
        return zkClient.exists(path);
    }

    public static String getRoot(){
        return root;
    }

    public static Boolean getIsZk(){
        return isZkSwitch;
    }
}
