package com.luz.hormone.handler;
import com.luz.hormone.constant.Constant;
import com.luz.hormone.service.MessageListService;
import com.luz.hormone.service.OfflinePushService;
import com.luz.hormone.service.UserConfigService;
import com.luz.hormone.utils.ChannelUtil;
import com.luz.hormone.utils.NettyAttrUtil;
import com.luz.hormone.utils.SpringUtils;
import com.luz.hormone.dataPackage.DataPackage;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@ChannelHandler.Sharable
public class MyDataHandler extends  SimpleChannelInboundHandler<DataPackage> {

    Logger log=LoggerFactory.getLogger(MyDataHandler.class);
    MessageListService messageListService;
    UserConfigService userConfigService;
    {
        userConfigService=SpringUtils.getObject(UserConfigService.class);
        messageListService=SpringUtils.getObject(MessageListService.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DataPackage data) throws Exception {
        log.info(data.toString());
        ServerHeartBeatHandler serverHeartBeatHandler=SpringUtils.getObject(ServerHeartBeatHandler.class);
        switch (data.getCmd()) {
            case Constant.CONNENT: {
              int userId = Integer.valueOf(new String(data.getData()));
              ChannelUtil.put(userId, channelHandlerContext);
                log.info("userId "+userId+" online");
              // 设置心跳超时
              NettyAttrUtil.saveHeart(channelHandlerContext, serverHeartBeatHandler.getHeart());
              // 保存路由关系
              userConfigService.saveRoute(userId);
              // 处理离线消息
              OfflinePushService offlinePushService = SpringUtils.getObject(OfflinePushService.class);
              offlinePushService.process(userId);
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
                  .writeAndFlush(dataPackage).addListener((ChannelFutureListener) future -> { if (!future.isSuccess()) { log.debug("io is error, Channel close");future.channel().close(); } });
            }
            break;
            //处理业务逻辑
            case Constant.MSG :{
                data=messageListService.process(data);
                channelHandlerContext
                        .writeAndFlush(data).addListener((ChannelFutureListener) future -> { if (!future.isSuccess()) { log.debug("io is error, Channel close");future.channel().close(); } });
            }
            break;
            case Constant.USER_EXIT:{
                int userId = Integer.valueOf(new String(data.getData()));
                ChannelUtil.removeChannelInfo(userId);
                userConfigService.delRoute(userId);
                channelHandlerContext.close();
                log.info("userId "+userId+" offline");
            }
        }

    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        int userId=ChannelUtil.getUserId(ctx);
        ChannelUtil.removeChannelInfo(ctx);
        userConfigService.delRoute(userId);
        // NOOP
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state()== IdleState.READER_IDLE){
                log.info("client 开始心跳检测");
                ServerHeartBeatHandler serverHeartBeatHandler=SpringUtils.getObject(ServerHeartBeatHandler.class);
                serverHeartBeatHandler.process(ctx);
            }
        }
        //调用另一个心跳检测器
        //super.userEventTriggered(ctx, evt);
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //int userId=ChannelUtil.getUserId(ctx);
        //userConfigService.delRoute(userId);
        ChannelUtil.removeChannelInfo(ctx);
        ctx.fireExceptionCaught(cause);
    }

}
