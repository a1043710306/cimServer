package com.luz.hormone.constant;

public class Constant {
    public static final int CONNENT=0x11;
    public static final int PING=0x21;
    public static final int MSG=0x30;
    public static final int PUSH=0x40;
    public static final int MAINTAIN=0X50;
    public static final int RESP_OK=200;
    public static final String Resp_OK_MESSAGE="ok";
    public static final String OFFLINE_MSG_INDEX="offline_msg_";
    public static final int OFFLINE_MSG_ATTR_DAY=60*60*24;
    public static final String USER_ROUTE_INDEX="route_";
    public static final int  TIME_SLOT=60*60*24;
    public static final String OFFLINE_PUSH_MSG_INDEX="offline_push_msg_";
    public static final int VIRTUAL_USER=1;

    public static final int USER_EXIT=0X00;

    public static class CHAT{
        public static final int CHAT_GROUP=1;
        public static final int CHAT_SOLO=2;
    }
    public static class METHOD{
        public static final int SEND=1;
        public static final int HISTORY=2;
        public static final int PUSH=3;
        public static final int MESSAGE_LIST=5; //获取消息列表
        public static final int DEL_MESSAGE_LIST=6;
    }
}
