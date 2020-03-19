package com.luz.hormone.netty;

import com.luz.hormone.constant.Constant;
import com.luz.hormone.dataPackage.DataPackage;
import com.luz.hormone.dataPackage.PackageCodec;
import com.luz.hormone.handler.MyDataHandler;
import com.luz.hormone.handler.MyWebSocketHandler;
import com.luz.hormone.utils.ChannelUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.Map;

@Component
public class NettyServer {
    Logger LOGGER=LoggerFactory.getLogger(NettyServer.class);
    @Value("${mynetty.heart}")
    private int heart;

    @Value("${mynetty.port}")
    private  int port;
    private NioEventLoopGroup boosGroup;
    private NioEventLoopGroup group;
    private ServerBootstrap serverBootstrap;
    {
        boosGroup=new NioEventLoopGroup();
        group=new NioEventLoopGroup();
        serverBootstrap=new ServerBootstrap();
    }

    /**
     * 启动服务
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
            LOGGER.info("Netty server starting ...");
            serverBootstrap.group(boosGroup,group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.AUTO_READ, true)
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new PackageCodec());
                            socketChannel.pipeline().addLast(new IdleStateHandler(heart, 0, 0));
                            socketChannel.pipeline().addLast(new MyDataHandler());
                            //网页端支持长连接
                            socketChannel.pipeline().addLast("http-codec",new HttpServerCodec());
                            socketChannel.pipeline().addLast("webSocket",new WebSocketServerProtocolHandler("/ws"));
                            socketChannel.pipeline().addLast(new MyWebSocketHandler());
                        }
                    });

            ChannelFuture channelFuture=serverBootstrap.bind(port).sync();
            if(channelFuture.isSuccess()){
                LOGGER.info("Netty server start");
            }
    }

    /**
     * 销毁 长连接
     */
    @PreDestroy
    public void destroy(){
        boosGroup.shutdownGracefully().syncUninterruptibly();
        group.shutdownGracefully().syncUninterruptibly();
        LOGGER.info("Netty server close");
    }

    /**
     *  发送消息
     * @param userId
     * @param msg
     * @return
     */
    public void sendMsg(int userId,String msg){
        ChannelHandlerContext channel= ChannelUtil.getChannel(userId);
        if (channel==null){
            throw  new NullPointerException("user "+userId+" offline");
        }
        DataPackage dataPackage=new DataPackage(msg.length(),msg.getBytes());
        dataPackage.setCode(Constant.METHOD.PUSH);
        dataPackage.setCommand(Constant.MSG);
        ChannelFuture channelFuture= channel.writeAndFlush(dataPackage);
        channelFuture.addListener((ChannelFutureListener) cf ->LOGGER.info(" SEND MESSAGE SUSS :  ->"+msg));
        return ;
    }

    /**
     *   自定义消息操作
     * @param userId
     * @param dataPackage
     */
    public void sendMsg(int userId ,DataPackage dataPackage){
        ChannelHandlerContext channel= ChannelUtil.getChannel(userId);
        if (channel==null){
            throw  new NullPointerException("user "+userId+" offline");
        }
        ChannelFuture channelFuture= channel.writeAndFlush(dataPackage);
        channelFuture.addListener((ChannelFutureListener) cf ->LOGGER.info(" SEND Command SUSS "+dataPackage.toString()));
    }

    /**
     * 关闭通道
     * @param userId
     */
    public void closeChannel(int userId){
        ChannelHandlerContext channel= ChannelUtil.getChannel(userId);
        if (channel==null){
            throw  new NullPointerException("user "+userId+" offline");
        }
        LOGGER.info("closing "+userId+" channel");
        channel.close();
    }
}
