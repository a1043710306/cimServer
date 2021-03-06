package com.luz.hormone.dataPackage;

import com.luz.hormone.handler.MyDataHandler;
import com.luz.hormone.handler.MyWebSocketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.springframework.core.codec.ByteBufferEncoder;

import java.util.List;

public class PackageCodec extends ByteToMessageCodec {

    private  final int CHECK_HEADER=16;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        DataPackage dataPackage=(DataPackage)o;
        byteBuf.writeInt(DataPackage.getPackageHeader());
        byteBuf.writeInt(dataPackage.getDataLength());
        byteBuf.writeInt(dataPackage.getCmd());
        byteBuf.writeInt(dataPackage.getCode());
        byteBuf.writeBytes(dataPackage.getData().getBytes());
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {

        if (byteBuf.readableBytes()<CHECK_HEADER){
            return;  //数据包不完整 继续等待数据
        }
        if (!isHeader(byteBuf)){
            return ;
        }

        int dataLength=byteBuf.readInt();
        int command=byteBuf.readInt();
        int code=byteBuf.readInt();
        if (dataLength>byteBuf.readableBytes()){
            byteBuf.resetReaderIndex();
            return;
        }
        byte data[]=new byte[dataLength];
        byteBuf.readBytes(data);
        DataPackage o=new DataPackage(dataLength,new String(data));
        o.setCmd(command);
        o.setCode(code);
        list.add(o);
    }

    private boolean isHeader(ByteBuf byteBuf){
        while(true){
            byteBuf.markReaderIndex();

            //检查包头
            int checkHeader=byteBuf.readInt();
            if (checkHeader==DataPackage.getPackageHeader()){
                break;
            }
            // 未读到包头，略过一个字节
            // 每次略过，一个字节，去读取，包头信息的开始标记
            byteBuf.resetReaderIndex();
            byteBuf.readByte();
            //数据包不完整 继续等待数据
            if (byteBuf.readableBytes()<CHECK_HEADER){
                return false;  //数据包不完整 继续等待数据
            }

        }
        return true;
    }
}
