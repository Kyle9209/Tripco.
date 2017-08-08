package com.tripco.www.tripco.model;

/**
 * Created by Tacademy on 2017-08-08.
 */

public class AlarmModel {

    String alarmContent;
    String alarmTime;
    int profile;

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
}
