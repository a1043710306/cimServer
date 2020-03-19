package com.luz.route.constant;

public class Constant {
    public static String USER_ROUTE_INDEX="route_";
    public static int VERIFI_CODE=0x66;
    public static final String OFFLINE_MSG_INDEX="offline_msg_";
    public static final int OFFLINE_MSG_ATTR_DAY=60*60*24;
    public static final int PUSH=0x40;
    public static final String OFFLINE_PUSH_MSG_INDEX="offline_push_msg_";
    public static class HTTP_CODE{
        public static int VERIFI_FAILD_CODE=401;
        public static String VERIFI__FAILD_CODE_MSG="Validation failed";
        public static int NO_USER=300;
        public static String NO_USER_MSG="There is no such user";
        public static int NO_NODE=100;
        public static String NO_NODE_MSG="No nodes available";
        public static int OK=200;
        public static String OK_MSG="ok";
    }
}
