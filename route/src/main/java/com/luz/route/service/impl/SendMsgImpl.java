package com.luz.route.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.luz.route.enetity.Node;
import com.luz.route.enetity.SendReqEntity;
import com.luz.route.service.OfflinePushService;
import com.luz.route.service.SendMsg;
import com.luz.route.service.UserConfigService;
import com.luz.route.utils.ThreadPoolConfig;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendMsgImpl implements SendMsg {
    private Logger LOGGER= LoggerFactory.getLogger(SendMsgImpl.class);
    @Autowired
    ThreadPoolConfig threadPoolConfig;
    @Autowired
    UserConfigService userConfigService;
    @Autowired
    OfflinePushService offlinePushService;

    @Override
    public void send(Node node,int userId, String msg) {
        threadPoolConfig.asyncExecutor().execute(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("send message to server");
                OkHttpClient okHttpClient=new OkHttpClient();
                SendReqEntity sendReqEntity=new SendReqEntity();
                sendReqEntity.setUserId(userId);
                sendReqEntity.setData(msg);
                String url="http://"+node.getIp()+":"+node.getWebPort()+"/sendMsg";
                final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

                okhttp3.RequestBody requestBody=okhttp3.RequestBody.create(JSON, JSONObject.toJSONString(sendReqEntity));
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try{
                    Response response=okHttpClient.newCall(request).execute();
                    if(response.isSuccessful()){
                        //打印服务端返回结果
                        LOGGER.info(response.body().toString());
                    }
                    response.body().close();
                }catch (Exception e){
                    LOGGER.info("saveOfflineMSG userId "+userId);
                    offlinePushService.saveOfflineMSG(userId,msg);
                    LOGGER.error(e.toString());
                }
            }
        });
    }

    @Override
    public void sendCmdMsg(Node node,int userId, int cmd, int code, String msg) {
        threadPoolConfig.asyncExecutor().execute(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("send message to server");
                OkHttpClient okHttpClient=new OkHttpClient();
                SendReqEntity sendReqEntity=new SendReqEntity();
                sendReqEntity.setUserId(userId);
                sendReqEntity.setCmd(cmd);
                sendReqEntity.setCode(code);
                sendReqEntity.setData(msg);
                String url="http://"+node.getIp()+":"+node.getWebPort()+"/sendCmdMsg";
                final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

                okhttp3.RequestBody requestBody=okhttp3.RequestBody.create(JSON, JSONObject.toJSONString(sendReqEntity));
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try{
                    Response response=okHttpClient.newCall(request).execute();
                    if(response.isSuccessful()){
                        //打印服务端返回结果
                        LOGGER.info(response.body().toString());
                    }
                    response.body().close();
                }catch (Exception e){
                    LOGGER.info("saveOfflineMSG userId "+userId);
                    offlinePushService.saveOfflineMSG(userId,msg);
                    LOGGER.error(e.toString());
                }
            }
        });
    }
}
