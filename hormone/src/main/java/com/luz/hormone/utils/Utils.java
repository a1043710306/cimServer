package com.luz.hormone.utils;

import com.luz.hormone.entity.HistoryMessageEntity;
import com.luz.hormone.entity.MessageListEntity;
import com.luz.hormone.entity.ServerMessagePush;
import com.luz.hormone.model.MessageListModel;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utils {
    public static void processUserId(HistoryMessageEntity historyMessageEntity,
                                                     MessageListModel messageListModel,int userId,int firendId){
        if (messageListModel.getUserId()==userId){
            try{
                historyMessageEntity.setIsSelf(1);
                historyMessageEntity.setUserId(messageListModel.getUserId());
                historyMessageEntity.setFriendUserId(messageListModel.getFriendUserId());
                historyMessageEntity.setMsg(URLDecoder.decode(messageListModel.getMsg(),"utf-8"));
                historyMessageEntity.setMsgType(messageListModel.getMsgType());
                historyMessageEntity.setCreateTime(messageListModel.getCreateTime());
            }catch (Exception e){
                return ;
            }
        }else {
            try{
                historyMessageEntity.setIsSelf(0);
                historyMessageEntity.setFriendUserId(messageListModel.getFriendUserId());
                historyMessageEntity.setUserId(messageListModel.getFriendUserId());
                historyMessageEntity.setMsg(URLDecoder.decode(messageListModel.getMsg(),"utf-8"));
                historyMessageEntity.setMsgType(messageListModel.getMsgType());
                historyMessageEntity.setCreateTime(messageListModel.getCreateTime());
            }catch (Exception e){
                return;
            }
        }
    }

    public static void processCircleMessage(HistoryMessageEntity historyMessageEntity,MessageListModel messageListModel,int userId){
        try{
            if (messageListModel.getUserId()==userId){
                historyMessageEntity.setIsSelf(1);
                historyMessageEntity.setUserId(messageListModel.getUserId());
                historyMessageEntity.setMsg(URLDecoder.decode(messageListModel.getMsg(),"utf-8"));
                historyMessageEntity.setMsgType(messageListModel.getMsgType());
                historyMessageEntity.setCreateTime(messageListModel.getCreateTime());
            }else {
                historyMessageEntity.setIsSelf(0);
                historyMessageEntity.setUserId(messageListModel.getUserId());
                historyMessageEntity.setMsg(URLDecoder.decode(messageListModel.getMsg(),"utf-8"));
                historyMessageEntity.setMsgType(messageListModel.getMsgType());
                historyMessageEntity.setCreateTime(messageListModel.getCreateTime());
            }
        }catch (Exception e){
            return ;
        }

    }


    public static MessageListModel getMessageModel(MessageListEntity entity){
           MessageListModel messageListModel=new MessageListModel();
           try{
               messageListModel.setCircleId(entity.getCircleId());
               messageListModel.setCreateTime((int)(System.currentTimeMillis()/1000));
               messageListModel.setFriendUserId(entity.getFriendUserId());
               messageListModel.setUserId(entity.getUserId());
               messageListModel.setType(entity.getType());
               messageListModel.setMsg(URLEncoder.encode(entity.getMsg(),"utf-8"));
               messageListModel.setMsgType(entity.getMsgType());
           } catch (Exception e) {
              return null;
           }
           return messageListModel;
    }

    public static ServerMessagePush getServerMessagePush(MessageListModel messageListModel,int userId){
        ServerMessagePush serverMessagePush=new ServerMessagePush();
        serverMessagePush.setCircleId(messageListModel.getCircleId());
        serverMessagePush.setCreateTime(messageListModel.getCreateTime());
        serverMessagePush.setMsg(messageListModel.getMsg());
        serverMessagePush.setUserId(messageListModel.getFriendUserId());
        serverMessagePush.setIsSelf(0);
        serverMessagePush.setFriendId(userId);
        serverMessagePush.setMsgType(messageListModel.getMsgType());
        return serverMessagePush;
    }

    public static String ProcessingParameterSpecialCharacters(String args){
        return args.substring(1,args.length()-1);
    }

    public static List<HistoryMessageEntity> sort( List<HistoryMessageEntity> messageEntities){
        Collections.sort(messageEntities, new Comparator<HistoryMessageEntity>() {
            @Override
            public int compare(HistoryMessageEntity o1, HistoryMessageEntity o2) {
                return o1.getCreateTime()-o2.getCreateTime();
            }
        });
        return messageEntities;
    }
}
