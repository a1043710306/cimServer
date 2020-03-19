package com.luz.route.utils;

import com.luz.route.enetity.Node;
import com.luz.route.zk.ZkServicer;

import java.util.ArrayList;
import java.util.List;

public class ZkitUtils {


    public static List<Node> getAllNode(){
        List<String> nodes=null;
        List<Node> nodeList=new ArrayList<>();
        try{
            nodes=ZkServicer.getAllNode(ZkServicer.getRoot());
        }catch (Exception e){
            return null;
        }
        if (nodes==null||nodes.size()==0){
            return null;
        }
        for (String ipAndPort : nodes) {
              Node node = new Node();
              String ip = ipAndPort.split(":")[0].substring(3);
              int port = Integer.valueOf(ipAndPort.split(":")[1]);
              int WebPort = Integer.valueOf(ipAndPort.split(":")[2]);
              node.setIp(ip);
              node.setPort(port);
              node.setWebPort(WebPort);
              nodeList.add(node);
        }
        return nodeList;
    }
}
