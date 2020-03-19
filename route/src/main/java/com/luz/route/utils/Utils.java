package com.luz.route.utils;

import com.luz.route.constant.Constant;

public class Utils {
    //验证Header参数
    public static Boolean verification(String code){
        try{
            if (Integer.valueOf(code)!= Constant.VERIFI_CODE){
                return false;
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
