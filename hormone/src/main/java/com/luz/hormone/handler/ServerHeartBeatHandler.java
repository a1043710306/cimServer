package com.luz.hormone.handler;

import com.luz.hormone.netty.NettyServer;
import com.luz.hormone.service.UserConfigService;
import com.luz.hormone.utils.ChannelUtil;
import com.luz.hormone.utils.NettyAttrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServerHeartBeatHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerHeartBeatHandler.class);
    @Value("${mynetty.heart}")
    private int heart;
    @Autowired
    UserConfigService userConfigService;
    @Autowired
    NettyServer nettyServer;
    public void process(ChannelHandlerContext ctx){
        if (NettyAttrUtil.checkHeart(ctx)){
        }
        //心跳超时  清理存储关系
        int userId=ChannelUtil.getUserId(ctx);
        ChannelUtil.removeChannelInfo(ctx);
        userConfigService.delRoute(userId);
        LOGGER.info(userId+" 心跳超时,关闭连接");
        nettyServer.closeChannel(userId);
    }

    public int getHeart() {
        return this.heart;
    }
}
