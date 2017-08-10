package com.tripco.www.tripco.model;

/**
 * Created by Tacademy on 2017-08-09.
 */

public class FinalScheduleModel {

    String time;
    int content;
    String location;
    int check;


    public FinalScheduleModel(String time, int content, String location, int check) {
        this.time = time;
        this.content = content;
        this.location = location;
        this.check = check;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}

