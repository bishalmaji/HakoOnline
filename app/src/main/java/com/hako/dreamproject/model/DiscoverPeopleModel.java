package com.hako.dreamproject.model;

public class DiscoverPeopleModel {
   private String user_id;
   private String points;
    private String userid;
    private String name;
    private String user_unique_id;
    private String profile;

    public DiscoverPeopleModel(String user_id, String points, String userid, String name, String user_unique_id, String profile) {
        this.user_id = user_id;
        this.points = points;

        this.userid = userid;
        this.name = name;
        this.user_unique_id = user_unique_id;
        this.profile = profile;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }


    public String getUser_unique_id() {
        return user_unique_id;
    }

    public void setUser_unique_id(String user_unique_id) {
        this.user_unique_id = user_unique_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
