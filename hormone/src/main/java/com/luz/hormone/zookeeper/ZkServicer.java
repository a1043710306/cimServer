package com.luz.hormone.zookeeper;


import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZkServicer {
    private static Logger LOGGER= LoggerFactory.getLogger(ZkServicer.class);

    @Value("${zk.root}")
    private String root;
    @Value("${zk.connection}")
    private String connection;
    @Value("${zk.iszk}")
    private boolean isZkSwitch;

    private  ZkClient zkClient;


    public synchronized void  init(){
        if (zkClient==null)
            zkClient=new ZkClient(connection);
    }

    /**
     * 创建父节点
     */
    public void createRootNode(){
        boolean exists=zkClient.exists(this.root);
        if (exists){
            return ;
        }
        zkClient.createPersistent(this.root);
    }
    /**
     * 写入指定节点 临时目录
     *
     * @param path
     */
    public void createNode(String path) {
        LOGGER.info("zk create node "+path);
        zkClient.createEphemeral(path);
    }

    /**
     * 删除节点
     * @param path
     */
    public void delNode(String path){
        LOGGER.info("zk del node "+path);
        zkClient.delete(path);
    }

    /**
     * 检查节点
     * @param path
     */
    public boolean checkNode(String path){
        return zkClient.exists(path);
    }

    public String getRoot(){
        return root;
    }

    public Boolean getIsZk(){
        return isZkSwitch;
    }
}
