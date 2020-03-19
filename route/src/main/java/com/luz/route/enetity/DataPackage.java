package com.luz.route.enetity;

import com.alibaba.fastjson.JSONObject;

public class DataPackage {
    //Package header
    private static final int PACKAGE_HEADER=0xCCCCCCCC;
    private int dataLength;
    private int command;
    private int code;
    private byte data[];
    public DataPackage(int dataLength, byte[] data){
        this.data=data;
        this.dataLength=dataLength;
    }

    public static int getPackageHeader() {
        return PACKAGE_HEADER;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String toString(){
        return "{ \"dataLength\":"+this.dataLength+","+
                "\"command\":"+this.command+","+
                "\"code\":"+this.code+","+
                "\"data\":"+ JSONObject.toJSONString(new String(this.data)) +"}";
    }
}
