package com.luz.hormone.handler;

import com.alibaba.fastjson.JSONObject;
import com.luz.hormone.constant.Constant;
import com.luz.hormone.dataPackage.DataPackage;
import com.luz.hormone.service.MessageListService;
import com.luz.hormone.service.OfflinePushService;
import com.luz.hormone.service.UserConfigService;
import com.luz.hormone.utils.ChannelUtil;
import com.luz.hormone.utils.NettyAttrUtil;
import com.luz.hormone.utils.SpringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;


public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    Logger log= LoggerFactory.getLogger(MyWebSocketHandler.class);
    MessageListService messageListService;
    UserConfigService userConfigService;
    {
        userConfigService= SpringUtils.getObject(UserConfigService.class);
        messageListService=SpringUtils.getObject(MessageListService.class);
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress()+" is online");
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress().toString());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ChannelUtil.removeChannelInfo_Web(ctx);
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
         //业务逻辑
        log.info(msg.text());
        DataPackage data=prossData(msg.text());
        ServerHeartBeatHandler serverHeartBeatHandler=SpringUtils.getObject(ServerHeartBeatHandler.class);
        switch (data.getCmd()) {
            case Constant.CONNENT: {
                int userId = Integer.valueOf(new String(data.getData()));
                ChannelUtil.put_Web(userId, channelHandlerContext);
                log.info("userId "+userId+" online");
                // 设置心跳超时
                NettyAttrUtil.saveHeart(channelHandlerContext, serverHeartBeatHandler.getHeart());
                // 保存路由关系
                userConfigService.saveRoute(userId);
                // 处理离线消息
                OfflinePushService offlinePushService = SpringUtils.getObject(OfflinePushService.class);
                offlinePushService.process(userId);
                DataPackage dataPackage =
                        new DataPackage(
                                Constant.Resp_OK_MESSAGE.length(), Constant.Resp_OK_MESSAGE);
                dataPackage.setCmd(Constant.PING);
                dataPackage.setCode(Constant.RESP_OK);
                channelHandlerContext
                        .writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(dataPackage)));
            }
            break;
            // 响应心跳包
            case  Constant.PING : {
                log.debug("resp ping " + channelHandlerContext.channel().remoteAddress());
                DataPackage dataPackage =
                        new DataPackage(
                                Constant.Resp_OK_MESSAGE.length(), Constant.Resp_OK_MESSAGE);
                dataPackage.setCmd(Constant.PING);
                dataPackage.setCode(Constant.RESP_OK);
                // 更新心跳时间
                NettyAttrUtil.saveHeart(channelHandlerContext, serverHeartBeatHandler.getHeart());
                channelHandlerContext
                        .writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(dataPackage)));
            }
            break;
            //处理业务逻辑
            case Constant.MSG :{
                data=messageListService.processWebMessage(data);
                channelHandlerContext
                        .writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(data)));
            }
            break;
            case Constant.USER_EXIT:{
                int userId = Integer.valueOf(new String(data.getData()));
                ChannelUtil.removeChannelInfo(userId);
                userConfigService.delRoute(userId);
                log.info("userId "+userId+" offline");
            }
            break;
        }
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state()== IdleState.READER_IDLE){
                log.info("websocket 开始心跳检测");
                ServerHeartBeatHandler serverHeartBeatHandler=SpringUtils.getObject(ServerHeartBeatHandler.class);
                serverHeartBeatHandler.process(ctx);
            }
        }
        //调用另一个心跳检测器
        //super.userEventTriggered(ctx, evt);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        ChannelUtil.removeChannelInfo_Web(ctx);
        ctx.close();
    }

    private DataPackage prossData(String msg){
        Map<String,Object> pars= JSONObject.parseObject(msg,Map.class);
        String dat;
        if (pars.get("data") instanceof Integer){
            dat=pars.get("data")+"";
        }else {
            dat=(String)pars.get("data");
        }
        DataPackage data=new DataPackage(dat.length(),dat);
        data.setCmd((Integer) pars.get("cmd"));
        data.setCode((Integer) pars.get("code"));
        return data;
    }

}
