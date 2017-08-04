package com.tripco.www.tripco.model;

/**
 * Created by kkmnb on 2017-08-03.
 */

public class LoginModel {
    String user_id;
    String user_pw;

    public LoginModel(String user_id, String user_pw) {
        this.user_id = user_id;
        this.user_pw = user_pw;
    }

    @Override
    public String toString() {
        return "LoginModel{" +
                "user_id='" + user_id + '\'' +
                ", user_pw='" + user_pw + '\'' +
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
}
