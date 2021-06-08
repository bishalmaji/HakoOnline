package com.hako.dreamproject.model;

public class GameDataModel {

    int cloudtrigger;
    int p1health;
    String p1name;
    int p1size;
    int p1spawn;
    String p1url;
    int p2health;
    String p2name;
    int p2size;
    int p2spawn;
    String p2url;

    public GameDataModel() { }

    public GameDataModel(int cloudtrigger, int p1health, String p1name, int p1size, int p1spawn, String p1url, int p2health, String p2name, int p2size, int p2spawn, String p2url) {
        this.cloudtrigger = cloudtrigger;
        this.p1health = p1health;
        this.p1name = p1name;
        this.p1size = p1size;
        this.p1spawn = p1spawn;
        this.p1url = p1url;
        this.p2health = p2health;
        this.p2name = p2name;
        this.p2size = p2size;
        this.p2spawn = p2spawn;
        this.p2url = p2url;
    }

    public int getCloudtrigger() {
        return cloudtrigger;
    }

    public void setCloudtrigger(int cloudtrigger) {
        this.cloudtrigger = cloudtrigger;
    }

    public int getP1health() {
        return p1health;
    }

    public void setP1health(int p1health) {
        this.p1health = p1health;
    }

    public String getP1name() {
        return p1name;
    }

    public void setP1name(String p1name) {
        this.p1name = p1name;
    }

    public int getP1size() {
        return p1size;
    }

    public void setP1size(int p1size) {
        this.p1size = p1size;
    }

    public int getP1spawn() {
        return p1spawn;
    }

    public void setP1spawn(int p1spawn) {
        this.p1spawn = p1spawn;
    }

    public String getP1url() {
        return p1url;
    }

    public void setP1url(String p1url) {
        this.p1url = p1url;
    }

    public int getP2health() {
        return p2health;
    }

    public void setP2health(int p2health) {
        this.p2health = p2health;
    }

    public String getP2name() {
        return p2name;
    }

    public void setP2name(String p2name) {
        this.p2name = p2name;
    }

    public int getP2size() {
        return p2size;
    }

    public void setP2size(int p2size) {
        this.p2size = p2size;
    }

    public int getP2spawn() {
        return p2spawn;
    }

    public void setP2spawn(int p2spawn) {
        this.p2spawn = p2spawn;
    }

    public String getP2url() {
        return p2url;
    }

    public void setP2url(String p2url) {
        this.p2url = p2url;
    }
}
