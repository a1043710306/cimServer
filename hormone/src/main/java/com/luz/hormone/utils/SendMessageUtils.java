package com.luz.hormone.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SendMessageUtils {
    static Logger LOGGER= LoggerFactory.getLogger(SendMessageUtils.class);
    public static void sendMsgToRoute(String url,int userId,String msg){
        Map<String,String> js=new HashMap<>();
        js.put("userId",userId+"");
        js.put("msg",msg);
        OkHttpClient okHttpClient=new OkHttpClient();
        final MediaType JSON=MediaType.parse("application/json; charset=utf-8");

        okhttp3.RequestBody requestBody=okhttp3.RequestBody.create(JSON, JSONObject.toJSONString(js));
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
            LOGGER.error(e.toString());
        }
    }
}
