package com.hako.dreamproject.model;

public class GameRequest {

    String requestId;
    String gameName;
    String roomId;
    String friendId;
    String requesterId;
    String url;
    String rotation;
    int status;

    public GameRequest() { }

    public GameRequest(String requestId, String gameName, String friendId, String url, String rotation, String requesterId, int status, String roomId) {
        this.requestId = requestId;
        this.gameName = gameName;
        this.friendId = friendId;
        this.url = url;
        this.rotation = rotation;
        this.requesterId = requesterId;
        this.status = status;
        this.roomId = roomId;
    }

    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getGameName() {
        return gameName;
    }
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getFriendId() {
        return friendId;
    }
    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getRequesterId() {
        return requesterId;
    }
    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getRotation() {
        return rotation;
    }
    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
