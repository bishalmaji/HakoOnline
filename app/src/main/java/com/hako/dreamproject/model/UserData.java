package com.hako.dreamproject.model;

public class UserData {
    private String userid;
    private String name;
    private String user_unique_id;
    private String profile;

    public UserData(String userid, String name, String user_unique_id, String profile) {
        this.userid = userid;
        this.name = name;
        this.user_unique_id = user_unique_id;
        this.profile = profile;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_unique_id() {
        return user_unique_id;
    }

    public void setUser_unique_id(String user_unique_id) {
        this.user_unique_id = user_unique_id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
