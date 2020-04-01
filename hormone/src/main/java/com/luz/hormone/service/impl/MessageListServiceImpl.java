package com.luz.hormone.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.luz.hormone.constant.Constant;
import com.luz.hormone.dao.MessageListMapper;
import com.luz.hormone.dao.OfflinePushDB;
import com.luz.hormone.dao.SessionListMapper;
import com.luz.hormone.dao.UserInfoMapper;
import com.luz.hormone.dataPackage.DataPackage;
import com.luz.hormone.entity.*;
import com.luz.hormone.model.MessageListModel;
import com.luz.hormone.model.SessionList;
import com.luz.hormone.model.UserModel;
import com.luz.hormone.netty.NettyServer;
import com.luz.hormone.netty.WebSocketServer;
import com.luz.hormone.service.MessageListService;
import com.luz.hormone.utils.*;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
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
    private WebSocketServer webSocketServer;
    @Autowired
    private ThreadPoolConfig threadPoolConfig;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private SessionListMapper sessionListMapper;

    @Value("${route.url}")
    private String routeUrl;
    @Value("${route.port}")
    private int routePort;

    @Override
    public DataPackage process(DataPackage dataPackage) {
        switch(dataPackage.getCode()){
            case Constant.METHOD.SEND:{

                MessageListEntity messageListEntity=JSONObject.parseObject(new String(dataPackage.getData()),MessageListEntity.class);
                LOGGER.info("Send Sevice start params: "+JSONObject.toJSONString(dataPackage));
                String data=this.sendMessage(messageListEntity);
                dataPackage.setData(data);
                dataPackage.setDataLength(data.getBytes().length);

            }
            break;
            case Constant.METHOD.HISTORY:{
                //dataPackage.setData(Utils.ProcessingParameterSpecialCharacters(dataPackage.getData()));
                MessageListEntity messageListEntity=JSONObject.parseObject(dataPackage.getData(),MessageListEntity.class);
                String data=this.getHistory(messageListEntity);
                dataPackage.setDataLength(data.getBytes().length);
                dataPackage.setData(data);
            }
            break;
        }
        LOGGER.info("Send Sevice end params: "+JSONObject.toJSONString(dataPackage));
        return dataPackage;
    }

    @Override
    public DataPackage processWebMessage(DataPackage dataPackage) {
        switch(dataPackage.getCode()){
            case Constant.METHOD.SEND:{
                MessageListEntity messageListEntity=JSONObject.parseObject(dataPackage.getData(),MessageListEntity.class);
                LOGGER.info("Send Sevice start params: "+JSONObject.toJSONString(dataPackage));
                String data=null;
                data= this.sendMessage(messageListEntity);
                dataPackage.setData(data);
                dataPackage.setDataLength(data.getBytes().length);

            }
            break;
            case Constant.METHOD.HISTORY:{
                MessageListEntity messageListEntity=JSONObject.parseObject(dataPackage.getData(),MessageListEntity.class);
                LOGGER.info("Send Sevice start params: "+JSONObject.toJSONString(dataPackage));
                String data=getVirHistory(messageListEntity);
                dataPackage.setDataLength(data.length());
                dataPackage.setData(data);
            }
            break;
            case Constant.METHOD.MESSAGE_LIST:{
                MessageListEntity messageListEntity=JSONObject.parseObject(dataPackage.getData(),MessageListEntity.class);
                LOGGER.info("Send Sevice start params: "+JSONObject.toJSONString(dataPackage));
                String sessionListEntities=getSessionList(messageListEntity);
                dataPackage.setDataLength(sessionListEntities.length());
                dataPackage.setData(sessionListEntities);
            }
            break;
            case Constant.METHOD.DEL_MESSAGE_LIST:{
                MessageListEntity messageListEntity=JSONObject.parseObject(dataPackage.getData(),MessageListEntity.class);
                LOGGER.info("Send Sevice start params: "+JSONObject.toJSONString(dataPackage));
                String resp=delSession(messageListEntity);
                dataPackage.setDataLength(resp.length());
                dataPackage.setData(resp);
            }
            break;
        }
        LOGGER.info("Send Sevice end params: "+JSONObject.toJSONString(dataPackage));
        return dataPackage;
    }
    /**
     *
     */
    private String delSession(MessageListEntity entity){
        int userId=entity.getUserId();
        int friendId=entity.getFriendUserId();
        int circleId=entity.getCircleId();
        Map<String,Object> resp=new HashMap<>();
        if (circleId==0){
            sessionListMapper.delSessionByUserIdAndFriendId(userId,friendId);
            resp.put("code",Constant.RESP_OK);
            resp.put("msg",Constant.Resp_OK_MESSAGE);
        }else {

        }
        return JSONObject.toJSONString(resp);
    }


    /**
     * 获取消息列表
     * @param entity
     * @return
     */
    private String getSessionList(MessageListEntity entity){
        int userId= entity.getUserId();
        int friendId=entity.getFriendUserId();
        Map<String,Object> resp=new HashMap<>();
        List<SessionListEntity> sessionListEntities=new ArrayList<>();
        List<SessionList> sessionLists=sessionListMapper.getSessionListByUserId(userId);
         for (SessionList sessionList:sessionLists){
             sessionListEntities.add(getMessaageCount(userId,sessionList));
         }
         resp.put("data",sessionListEntities);
         resp.put("code",Constant.RESP_OK);
         resp.put("msg",Constant.Resp_OK_MESSAGE);
        return JSONObject.toJSONString(resp);
    }

    /**
     * 获取该会话的未读消息
     * @param userId
     * @param sessionList
     * @return
     */
    private SessionListEntity getMessaageCount(int userId,SessionList sessionList){
        SessionListEntity sessionListEntity=new SessionListEntity();
        sessionListEntity.setFriendId(sessionList.getFriendId());
        UserModel userModel=userInfoMapper.getUserInfoByUserId(sessionList.getFriendId());
        sessionListEntity.setFriendName(userModel.getUsername());
        sessionListEntity.setFriendIcon(JSONObject.parseArray(userModel.getHeadpicJson(), ImageEntity.class));
        int lastId=sessionList.getLastMessageId(),count=0;
        List<MessageListModel> messageListModels=messageListMapper.getHistoryNotByTime(userId,sessionList.getFriendId());
        for (count=0;count<messageListModels.size();count++){
                MessageListModel messageListModel=messageListModels.get(count);
            if (lastId==messageListModel.getId()){
                break;
            }
        }
        sessionListEntity.setMessageCount(messageListModels.size()-count);
        return sessionListEntity;
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
            this.CheckAndInsertSession(userId,friendId);
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
            if(ChannelUtil.getChannel(userId)!=null) {
                nettyServer.sendMsg(userId, msg);
            }else {
                webSocketServer.sendMsg(userId,msg);
            }
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

    /**
     * Web端获取历史消息
     * @param messageListEntity
     * @return
     */
    public String getVirHistory(MessageListEntity messageListEntity) {
        int userId=messageListEntity.getUserId();
        int friendUserId=messageListEntity.getFriendUserId();
        int time=messageListEntity.getTime();
        int circleId=messageListEntity.getCircleId();
        String returnMsg=null;
        //私聊消息
        if (circleId==0){
            List<HistoryMessageEntity> messages=new ArrayList<>(0);
            List<MessageListModel> msgs=this.selectWebMessage(userId,friendUserId);
            processVirMessageInRedis(userId,friendUserId);
            if (msgs.size()>0)
                UpdateSessionList(userId,msgs.get(0));
            for (MessageListModel m:msgs){
                HistoryMessageEntity historyMessageEntity=new HistoryMessageEntity();
                Utils.processUserId(historyMessageEntity,m,userId,friendUserId);
                messages.add(historyMessageEntity);
            }
            Utils.sort(messages);
            returnMsg= JSONObject.toJSONString(messages);
        }else {
            List<HistoryMessageEntity> messages=new ArrayList<>(0);
            List<MessageListModel> msgs=this.selectWebMessage(userId,circleId);
            processVirMessageInRedis(userId,friendUserId);
            if (msgs.size()>0)
                UpdateSessionList(userId,msgs.get(0));
            for (MessageListModel m:msgs){
                HistoryMessageEntity historyMessageEntity=new HistoryMessageEntity();
                Utils.processCircleMessage(historyMessageEntity,m,userId);
                messages.add(historyMessageEntity);
            }
            Utils.sort(messages);
            returnMsg= JSONObject.toJSONString(messages);
        }
        return returnMsg;
    }

    /**
     * Web
     * @param userId
     * @param friendUserId
     * @return
     */
    private List<MessageListModel> selectWebMessage(int userId,int friendUserId){

        List<MessageListModel> msgs=messageListMapper.getHistoryNotByTime(userId,friendUserId);
        if (msgs != null) {
            return msgs;
        }
        return new ArrayList<>(0);
    }


    /**
     * 获取历史消息
     * @param messageListEntity
     * @return
     */
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
            if (msgs.size()>0)
                UpdateSessionList(userId,msgs.get(0));
            for (MessageListModel m:msgs){
                HistoryMessageEntity historyMessageEntity=new HistoryMessageEntity();
                Utils.processUserId(historyMessageEntity,m,userId,friendUserId);
                messages.add(historyMessageEntity);
            }
            Utils.sort(messages);
            returnMsg= JSONObject.toJSONString(messages);
        }else {
            List<HistoryMessageEntity> messages=new ArrayList<>(0);
            List<MessageListModel> msgs=this.selectCircleMessage(userId,circleId,time);
            if (msgs.size()>0)
                UpdateSessionList(userId,msgs.get(0));
            for (MessageListModel m:msgs){
                HistoryMessageEntity historyMessageEntity=new HistoryMessageEntity();
                Utils.processCircleMessage(historyMessageEntity,m,userId);
                messages.add(historyMessageEntity);
            }
            Utils.sort(messages);
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

        List<MessageListModel> msgs=messageListMapper.getHistory(userId,friendUserId,time-Constant.TIME_SLOT,time);
        if (msgs != null) {
          return msgs;
        }
        return new ArrayList<>(0);
    }

    /**
     * 查询 群聊消息
     */
    public List<MessageListModel> selectCircleMessage(int userId,int circleId,int time){
        List<MessageListModel> msgs=messageListMapper.getHistoryByUserIdAndCircleId(circleId,time,time+Constant.TIME_SLOT);

        return msgs!=null?msgs:new ArrayList<>(0);
    }

    public void UpdateSessionList(int userId,MessageListModel messageListModel){
        int id=messageListModel.getId();
        int friend=messageListModel.getUserId()==userId?messageListModel.getFriendUserId():messageListModel.getUserId();
        sessionListMapper.updateMessageIdByUserIdAndfriendId(userId,friend,id);
    }

    public void CheckAndInsertSession(int userId,int friendId){
        //代码顺序不可更改  有业务逻辑
        SessionList sessionList=new SessionList();
        sessionList.setIsDel(0);
        if (sessionListMapper.getSessionByUserIdAndFriendId(userId,friendId)==null){
            sessionList.setUserId(userId);
            sessionList.setFriendId(friendId);
            sessionListMapper.insert(sessionList);
        }
        if (sessionListMapper.getSessionByUserIdAndFriendId(friendId,userId)==null){
            sessionList.setUserId(friendId);
            sessionList.setFriendId(userId);
            sessionListMapper.insert(sessionList);
        }
        return;
    }

    /**
     * 处理已读
     * @param virUserId
     * @param friendId
     */
    public void processVirMessageInRedis(int virUserId, int friendId){
        List<String> msg=OfflinePushDB.getOfflineMSG(virUserId)!=null?OfflinePushDB.getOfflineMSG(virUserId):new ArrayList<>();
        for (String meesage:msg){
            ServerMessagePush serverMessagePush=JSONObject.parseObject(meesage,ServerMessagePush.class);
            if (serverMessagePush.getFriendId()==friendId){
                continue;
            }else {
                OfflinePushDB.saveOfflineMSG(virUserId,meesage);
            }
        }

    }
}
