package com.luz.hormone.dataPackage;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class DataPackage implements Serializable {
    //Package header
    private static int Header=0xCCCCCCCC;
    private int dataLength;
    private int cmd;
    private int code;
    private String data;
    public DataPackage(int dataLength,String data){
        this.data=data;
        this.dataLength=dataLength;
    }

    public static  int getPackageHeader() {
        return Header;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
