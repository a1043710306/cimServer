package com.luz.hormone.entity;

import java.io.Serializable;
import java.util.List;

public class SessionListEntity  implements Serializable {
    private int messageCount;
    private int friendId;
    private String friendName;
    private int circleId;
    private List<ImageEntity> friendIcon;

    public List<ImageEntity> getFriendIcon() {
        return friendIcon;
    }

    public void setFriendIcon(List<ImageEntity> friendIcon) {
        this.friendIcon = friendIcon;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public int getCircleId() {
        return circleId;
    }

    public void setCircleId(int circleId) {
        this.circleId = circleId;
    }
}
