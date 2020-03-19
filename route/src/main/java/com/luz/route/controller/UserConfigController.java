package com.luz.route.controller;

import com.alibaba.fastjson.JSONObject;
import com.luz.route.constant.Constant;
import com.luz.route.enetity.*;
import com.luz.route.service.OfflinePushService;
import com.luz.route.service.SendMsg;
import com.luz.route.service.UserConfigService;
import com.luz.route.utils.ThreadPoolConfig;
import com.luz.route.utils.Utils;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@RestController
public class UserConfigController {
    @Autowired
    UserConfigService userConfigService;
    @Autowired
    OfflinePushService offlinePushService;
    @Autowired
    SendMsg sendMsg;

    Logger LOGGER= LoggerFactory.getLogger(UserConfigController.class);

    /**
     * 获取路由
     * @param
     * @return
     */
    @RequestMapping(value = "getRoute",method = RequestMethod.POST)
    @ResponseBody
    public RouteResp getRoute(@RequestHeader("verifi-code") String code){
        RouteResp routeResp=new RouteResp();
        if (!Utils.verification(code)) {
            routeResp.setCode(Constant.HTTP_CODE.VERIFI_FAILD_CODE);
            routeResp.setMsg(Constant.HTTP_CODE.VERIFI__FAILD_CODE_MSG);
            return routeResp;
        }
        List<Node> node=userConfigService.getRoute();
        if (node==null){
            routeResp.setCode(Constant.HTTP_CODE.NO_NODE);
            routeResp.setMsg(Constant.HTTP_CODE.NO_NODE_MSG);
            return routeResp;
        }
        routeResp.setCode(Constant.HTTP_CODE.OK);
        routeResp.setMsg(Constant.HTTP_CODE.OK_MSG);
        routeResp.setData(node);
        return routeResp;
    }
    /**
     * 转发消息
     * @param
     * @return
     */
    @RequestMapping(value = "sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public RouteResp sendMsg(@RequestBody RouteReq routeReq)  {
        RouteResp routeResp=new RouteResp();
        Node node=userConfigService.checkUserRoute(routeReq.getUserId());
        if (node==null){
            LOGGER.info("saveOfflineMSG userId "+routeReq.getUserId());
            offlinePushService.saveOfflineMSG(routeReq.getUserId(),routeReq.getMsg());
            routeResp.setCode(Constant.HTTP_CODE.OK);
            routeResp.setMsg(Constant.HTTP_CODE.OK_MSG);
            return routeResp;
        }

        sendMsg.send(node,routeReq.getUserId(),routeReq.getMsg());
        routeResp.setCode(Constant.HTTP_CODE.OK);
        routeResp.setMsg(Constant.HTTP_CODE.OK_MSG);
        return routeResp;
    }

    @RequestMapping(value = "sendPushByuserId",method = RequestMethod.POST)
    @ResponseBody
    public RouteResp sendPushByuserId(@RequestBody RouteReq routeReq){
        RouteResp routeResp=new RouteResp();
        int cmd=Constant.PUSH;
        int code=3;
        int userId=routeReq.getUserId();
        String msg=routeReq.getMsg();
        Node node=userConfigService.checkUserRoute(routeReq.getUserId());
        DataPackage dataPackage=new DataPackage(msg.getBytes().length,msg.getBytes());
        if (node==null){
            LOGGER.info("saveOfflineMSG userId "+routeReq.getUserId());
            offlinePushService.saveOfflinePushMSG(routeReq.getUserId(),dataPackage);
            routeResp.setCode(Constant.HTTP_CODE.OK);
            routeResp.setMsg(Constant.HTTP_CODE.OK_MSG);
            return routeResp;
        }
        sendMsg.sendCmdMsg(node,userId,cmd,code,msg);
        routeResp.setCode(Constant.HTTP_CODE.OK);
        routeResp.setMsg(Constant.HTTP_CODE.OK_MSG);
        return routeResp;
    }

}
