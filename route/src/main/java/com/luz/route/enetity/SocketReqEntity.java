package com.luz.route.enetity;

import java.io.Serializable;

public class SocketReqEntity implements Serializable {
    private int userId;
    private int cmd;
    private String data;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
}
