package com.hako.dreamproject.model;

public class chatRoom {
    private String chatRoomId;
    private String  myScore;
    private String friendId;
    private String friendName;
    private String  friendScore;
    private String friendProfile;
    private String play;
    private boolean online;

    public chatRoom(){ }

    public chatRoom(String chatRoomId, String myScore, String friendId, String friendName, String friendScore, String friendProfile, String play, boolean online) {
        this.chatRoomId = chatRoomId;
        this.myScore = myScore;
        this.friendId = friendId;
        this.friendName = friendName;
        this.friendScore = friendScore;
        this.friendProfile = friendProfile;

        this.play = play;
        this.online = online;
    }

    public String getMyScore() {
        return myScore;
    }

    public void setMyScore(String myScore) {
        this.myScore = myScore;
    }

    public String getFriendScore() {
        return friendScore;
    }

    public void setFriendScore(String  friendScore) {
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

    public String  isFirstMsg() {
        return play;
    }

    public void setFirstMsg(String  firstMsg) {
        this.play = play;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
