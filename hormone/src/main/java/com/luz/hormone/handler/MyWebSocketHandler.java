package com.luz.hormone.handler;

import com.luz.hormone.service.MessageListService;
import com.luz.hormone.service.UserConfigService;
import com.luz.hormone.utils.ChannelUtil;
import com.luz.hormone.utils.SpringUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
        // NOOP
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        int userId= ChannelUtil.getUserId(ctx);
        ChannelUtil.removeChannelInfo(ctx);
        userConfigService.delRoute(userId);
        // NOOP
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{

        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state()== IdleState.READER_IDLE){
                log.info("开始心跳检测");
                ServerHeartBeatHandler serverHeartBeatHandler=SpringUtils.getObject(ServerHeartBeatHandler.class);
                serverHeartBeatHandler.process(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        int userId=ChannelUtil.getUserId(ctx);
        userConfigService.delRoute(userId);
        ctx.fireExceptionCaught(cause);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
         //业务逻辑
    }
}
