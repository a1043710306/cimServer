package com.luz.hormone.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.luz.hormone.constant.Constant;
import com.luz.hormone.dao.MessageListMapper;
import com.luz.hormone.dataPackage.DataPackage;
import com.luz.hormone.entity.HistoryMessageEntity;
import com.luz.hormone.entity.MessageListEntity;
import com.luz.hormone.entity.ServerMessagePush;
import com.luz.hormone.model.MessageListModel;
import com.luz.hormone.netty.NettyServer;
import com.luz.hormone.service.MessageListService;
import com.luz.hormone.utils.NettyAttrUtil;
import com.luz.hormone.utils.SendMessageUtils;
import com.luz.hormone.utils.ThreadPoolConfig;
import com.luz.hormone.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageListServiceImpl implements MessageListService {
    private Logger LOGGER= LoggerFactory.getLogger(MessageListService.class);
    @Autowired
    private MessageListMapper messageListMapper;
    @Autowired
    private NettyServer nettyServer;
    @Autowired
    private ThreadPoolConfig threadPoolConfig;

    @Value("${route.url}")
    private String routeUrl;
    @Value("${route.port}")
    private int routePort;

    @Override
    public DataPackage process(DataPackage dataPackage) {
        switch(dataPackage.getCode()){
            case Constant.METHOD.SEND:{

                MessageListEntity messageListEntity=JSONObject.parseObject(new String(dataPackage.getData()),MessageListEntity.class);
                LOGGER.info("Send Sevice start params: "+new String(dataPackage.getData()));
                String data=this.sendMessage(messageListEntity);
                dataPackage.setData(data.getBytes());
                dataPackage.setDataLength(data.getBytes().length);
                LOGGER.info("Send Sevice end params: "+data);
            }
            break;
            case Constant.METHOD.HISTORY:{
                MessageListEntity messageListEntity=JSONObject.parseObject(new String(dataPackage.getData()),MessageListEntity.class);
                String data=this.getHistory(messageListEntity);
                dataPackage.setDataLength(data.getBytes().length);
                dataPackage.setData(data.getBytes());
            }
            break;
        }
        return dataPackage;
    }

    @Override
    public String sendMessage(MessageListEntity messageListEntity) {
        int friendId=messageListEntity.getFriendUserId();
        int userId=messageListEntity.getUserId();
        Map<String,Object> resp=new HashMap<>();
        //私聊
        if (Constant.CHAT.CHAT_SOLO==messageListEntity.getType()){
            MessageListModel messageListModel=Utils.getMessageModel(messageListEntity);
            this.saveMessage(messageListModel);
            ServerMessagePush serverMessagePush=Utils.getServerMessagePush(messageListModel,userId);
            String msg=JSONObject.toJSONString(serverMessagePush);
            this.sendByUserId(msg,friendId);
            resp.put("code",Integer.valueOf(200));
            resp.put("msg","ok");
            resp.put("createTime",Integer.valueOf(serverMessagePush.getCreateTime()));

        }else {
            //群聊扩展
        }
        return JSONObject.toJSONString(resp);
    }

    /**
     *
     */
    private void sendByUserId(String msg,int userId){
        final String url="http://"+routeUrl+":"+routePort+"/sendMsg";
        try{
            nettyServer.sendMsg(userId,msg);
        }catch (Exception e){
            //抛异常 将消息抛给路由处理
            LOGGER.warn("relay the message : "+userId+" :"+msg);
      threadPoolConfig.asyncExecutor()
          .execute(
              new Runnable() {
                @Override
                public void run() {
                  SendMessageUtils.sendMsgToRoute(url,userId,msg);
                }
              });
        }
    }




    /**
     *
     * @param messageListModel
     */
    private void saveMessage(MessageListModel messageListModel){
        messageListMapper.insert(messageListModel);
    }



    @Override
    public String getHistory(MessageListEntity messageListEntity) {
        int userId=messageListEntity.getUserId();
        int friendUserId=messageListEntity.getFriendUserId();
        int time=messageListEntity.getTime();
        int circleId=messageListEntity.getCircleId();
        String returnMsg=null;
        //私聊消息
        if (circleId==0){
            List<HistoryMessageEntity> messages=new ArrayList<>(0);
            List<MessageListModel> msgs=this.selectMessage(userId,friendUserId,time);

            for (MessageListModel m:msgs){
                HistoryMessageEntity historyMessageEntity=new HistoryMessageEntity();
                Utils.processUserId(historyMessageEntity,m,userId,friendUserId);
                messages.add(historyMessageEntity);
            }
            returnMsg= JSONObject.toJSONString(messages);
        }else {
            List<HistoryMessageEntity> messages=new ArrayList<>(0);
            List<MessageListModel> msgs=this.selectCircleMessage(userId,circleId,time);
            for (MessageListModel m:msgs){
                HistoryMessageEntity historyMessageEntity=new HistoryMessageEntity();
                Utils.processCircleMessage(historyMessageEntity,m,userId);
                messages.add(historyMessageEntity);
            }
            returnMsg= JSONObject.toJSONString(messages);
        }
        return returnMsg;
    }

    /**
     * 查询好友信息
     * @param userId
     * @param friendUserId
     * @param time
     * @return
     */
    public List<MessageListModel> selectMessage(int userId,int friendUserId,int time){

        List<MessageListModel> msgs=messageListMapper.getHistory(userId,friendUserId,time,time+Constant.TIME_SLOT);
        List<MessageListModel> msgs2=messageListMapper.getHistory(friendUserId,userId,time,time+Constant.TIME_SLOT);
        if (msgs!=null&&msgs2!=null){
            msgs.addAll(msgs2);
            return msgs;
        }else if (msgs!=null){
            return msgs;
        }else if (msgs2!=null){
            return msgs2;
        }
        return new ArrayList<>(0);
    }

    /**
     * 查询 群聊消息
     */
    public List<MessageListModel> selectCircleMessage(int userId,int circleId,int time){
        List<MessageListModel> msgs=messageListMapper.getHistoryByUserIdAndCircleId(userId,circleId,time,time+Constant.TIME_SLOT);

        return msgs!=null?msgs:new ArrayList<>(0);
    }

}
