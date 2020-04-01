package com.luz.hormone.controller;
import com.alibaba.fastjson.JSONObject;
import com.luz.hormone.constant.Constant;
import com.luz.hormone.dataPackage.DataPackage;
import com.luz.hormone.entity.SendReqEntity;
import com.luz.hormone.entity.SendRespEntity;
import com.luz.hormone.netty.NettyServer;
import com.luz.hormone.netty.WebSocketServer;
import com.luz.hormone.service.OfflinePushService;
import com.luz.hormone.service.UserConfigService;
import com.luz.hormone.utils.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.bind.annotation.*;


@RestController
public class MessageController {
    @Autowired
    private OfflinePushService offlinePushService;
    @Autowired
    private NettyServer nettyServer;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private UserConfigService userConfigService;

    /**
     * 发送消息
     * @param sendReqEntity
     * @return
     */
    @RequestMapping(value = "sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public SendRespEntity sendMsg(@RequestBody SendReqEntity sendReqEntity){
        SendRespEntity sendRespEntity=new SendRespEntity();
        try{
            if(ChannelUtil.getChannel(sendReqEntity.getUserId())!=null) {
                nettyServer.sendMsg(sendReqEntity.getUserId(), sendReqEntity.getData());
            }else {
                webSocketServer.sendMsg(sendReqEntity.getUserId(),sendReqEntity.getData());
            }
        }catch (Exception e){

            offlinePushService.saveOfflineMSG(sendReqEntity.getUserId(),sendReqEntity.getData());
        }
        sendRespEntity.setCode(Constant.RESP_OK);
        sendRespEntity.setMsg(Constant.Resp_OK_MESSAGE);
        return sendRespEntity;
    }

    /**
     * 发送带命令的消息
     * @param sendReqEntity
     * @return
     */
    @RequestMapping(value = "sendCmdMsg",method = RequestMethod.POST)
    @ResponseBody
    public SendRespEntity sendCmdMsg(@RequestBody SendReqEntity sendReqEntity){
        SendRespEntity sendRespEntity=new SendRespEntity();
        String data=sendReqEntity.getData();
        int cmd=sendReqEntity.getCmd();
        DataPackage msg=new DataPackage(data.getBytes().length,data);
        msg.setCmd(cmd);
        msg.setCode(sendReqEntity.getCode());
        try{
            nettyServer.sendMsg(sendReqEntity.getUserId(),msg);
        }catch (Exception e){
         offlinePushService.saveOfflinePushMSG(sendReqEntity.getUserId(),msg);
        }
        sendRespEntity.setCode(Constant.RESP_OK);
        sendRespEntity.setMsg(Constant.Resp_OK_MESSAGE);
        return sendRespEntity;
    }

    /**
     * 清除路由关系,关闭通道
     * @param sendReqEntity
     * @return
     */
    @RequestMapping(value = "delRoute",method = RequestMethod.POST)
    @ResponseBody
    public SendRespEntity delRoute(@RequestBody SendReqEntity sendReqEntity){
        SendRespEntity sendRespEntity=new SendRespEntity();
        nettyServer.closeChannel(sendReqEntity.getUserId());
        ChannelUtil.removeChannelInfo(sendReqEntity.getUserId());
        userConfigService.delRoute(sendReqEntity.getUserId());
        sendRespEntity.setCode(Constant.RESP_OK);
        sendRespEntity.setMsg(Constant.Resp_OK_MESSAGE);
        return sendRespEntity;
    }
}
