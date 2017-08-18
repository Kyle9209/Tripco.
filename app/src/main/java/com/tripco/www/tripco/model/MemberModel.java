package com.tripco.www.tripco.model;

public class MemberModel {
    int user_no;
    String user_id;
    String user_pw;
    String user_token;
    String user_uuid;
    String user_nick;
    String user_image;
    int user_yn;

    // 회원의 전체 데이터 그릇
    public MemberModel(int user_no, String user_id, String user_pw, String user_token, String user_uuid, String user_nick, String user_image, int user_yn) {
        this.user_no = user_no;
        this.user_id = user_id;
        this.user_pw = user_pw;
        this.user_token = user_token;
        this.user_uuid = user_uuid;
        this.user_nick = user_nick;
        this.user_image = user_image;
        this.user_yn = user_yn;
    }

    // 회원가입할 때 주는 그릇
    public MemberModel(String user_id, String user_pw, String user_token, String user_uuid, String user_nick) {
        this.user_id = user_id;
        this.user_pw = user_pw;
        this.user_token = user_token;
        this.user_uuid = user_uuid;
        this.user_nick = user_nick;
    }

    // 로그인 할 때 주는 그릇
    public MemberModel(String user_id, String user_pw) {
        this.user_id = user_id;
        this.user_pw = user_pw;
    }

    public int getUser_no() {
        return user_no;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
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

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public int getUser_yn() {
        return user_yn;
    }

    public void setUser_yn(int user_yn) {
        this.user_yn = user_yn;
    }
}
