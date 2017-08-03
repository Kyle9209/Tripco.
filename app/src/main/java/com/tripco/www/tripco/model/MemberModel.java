package com.tripco.www.tripco.model;

public class MemberModel {
    String user_id;
    String user_pw;
    String user_token;
    String user_uuid;
    String user_nick;

    public MemberModel(String user_id, String user_pw, String user_token, String user_uuid, String user_nick) {
        this.user_id = user_id;
        this.user_pw = user_pw;
        this.user_token = user_token;
        this.user_uuid = user_uuid;
        this.user_nick = user_nick;
    }

    @Override
    public String toString() {
        return "MemberModel{" +
                "user_id='" + user_id + '\'' +
                ", user_pw='" + user_pw + '\'' +
                ", user_token='" + user_token + '\'' +
                ", user_uuid='" + user_uuid + '\'' +
                ", user_nick='" + user_nick + '\'' +
                '}';
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_pw() {
        return user_pw;
    }

    public void setUser_pw(String user_pw) {
        this.user_pw = user_pw;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getUser_uuid() {
        return user_uuid;
    }

    public void setUser_uuid(String user_uuid) {
        this.user_uuid = user_uuid;
    }

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }
}
