package com.tripco.www.tripco.model;

/**
 * Created by Tacademy on 2017-08-08.
 */

public class AlarmModel {

    String alarmContent;
    String alarmTime;
    int profile;

    // 알람 클라우드
    int notice_no;
    int trip_no;
    String notice_trip;
    String notice_partner;
    String notice_image;
    String notice_item;
    String notice_type;
    String notice_time;


    public AlarmModel(int notice_no, int trip_no, String notice_trip, String notice_partner, String notice_image, String notice_item, String notice_type, String notice_time) {
        this.notice_no = notice_no;
        this.trip_no = trip_no;
        this.notice_trip = notice_trip;
        this.notice_partner = notice_partner;
        this.notice_image = notice_image;
        this.notice_item = notice_item;
        this.notice_type = notice_type;
        this.notice_time = notice_time;
    }

    public AlarmModel(String alarmContent, String alarmTime) {
        this.alarmContent = alarmContent;
        this.alarmTime = alarmTime;
        this.profile = profile;
    }

    public String getAlarmContent() {
        return alarmContent;
    }

    public void setAlarmContent(String alarmContent) {
        this.alarmContent = alarmContent;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public int getNotice_no() {
        return notice_no;
    }

    public void setNotice_no(int notice_no) {
        this.notice_no = notice_no;
    }

    public int getTrip_no() {
        return trip_no;
    }

    public void setTrip_no(int trip_no) {
        this.trip_no = trip_no;
    }

    public String getNotice_trip() {
        return notice_trip;
    }

    public void setNotice_trip(String notice_trip) {
        this.notice_trip = notice_trip;
    }

    public String getNotice_partner() {
        return notice_partner;
    }

    public void setNotice_partner(String notice_partner) {
        this.notice_partner = notice_partner;
    }

    public String getNotice_item() {
        return notice_item;
    }

    public void setNotice_item(String notice_item) {
        this.notice_item = notice_item;
    }

    public String getNotice_type() {
        return notice_type;
    }

    public void setNotice_type(String notice_type) {
        this.notice_type = notice_type;
    }

    public String getNotice_time() {
        return notice_time;
    }

    public void setNotice_time(String notice_time) {
        this.notice_time = notice_time;
    }

    public String getNotice_image() {return notice_image;}

    public void setNotice_image(String notice_image) {this.notice_image = notice_image;}
}