package com.hako.dreamproject.model;

public class chatRoom {
    private String chatRoomId;
    private int myScore;
    private String friendId;
    private String friendName;
    private int friendScore;
    private String friendProfile;

    public chatRoom(){ }

    public chatRoom(String chatRoomId, int myScore, String friendId, String friendName, int friendScore, String friendProfile) {
        this.chatRoomId = chatRoomId;
        this.myScore = myScore;
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendScore = friendScore;
        this.friendProfile = friendProfile;
    }

    public int getMyScore() {
        return myScore;
    }

    public void setMyScore(int myScore) {
        this.myScore = myScore;
    }

    public int getFriendScore() {
        return friendScore;
    }

    public void setFriendScore(int friendScore) {
        this.friendScore = friendScore;
    }

    public String getFriendProfile() {
        return friendProfile;
    }

    public void setFriendProfile(String friendProfile) {
        this.friendProfile = friendProfile;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
}
