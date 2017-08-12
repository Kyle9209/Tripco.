package com.tripco.www.tripco.model;

/**
 * Created by kkmnb on 2017-08-08.
 */

public class ScheduleModel {
    int trip_no;
    int schedule_no;
    String schedule_date;
    String item_url;
    String cate_no;
    String item_lat;
    String item_long;
    String item_location;
    String item_title;
    String item_memo;
    int item_check;
    String item_time;

    public ScheduleModel(int trip_no, int schedule_no, String schedule_date, String item_url, String cate_no, String item_lat, String item_long, String item_location, String item_title, String item_memo, int item_check, String item_time) {
        this.trip_no = trip_no;
        this.schedule_no = schedule_no;
        this.schedule_date = schedule_date;
        this.item_url = item_url;
        this.cate_no = cate_no;
        this.item_lat = item_lat;
        this.item_long = item_long;
        this.item_location = item_location;
        this.item_title = item_title;
        this.item_memo = item_memo;
        this.item_check = item_check;
        this.item_time = item_time;
    }

    public int getTrip_no() {
        return trip_no;
    }

    public void setTrip_no(int trip_no) {
        this.trip_no = trip_no;
    }

    public int getSchedule_no() {
        return schedule_no;
    }

    public void setSchedule_no(int schedule_no) {
        this.schedule_no = schedule_no;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getItem_url() {
        return item_url;
    }

    public void setItem_url(String item_url) {
        this.item_url = item_url;
    }

    public String getCate_no() {
        return cate_no;
    }

    public void setCate_no(String cate_no) {
        this.cate_no = cate_no;
    }

    public String getItem_lat() {
        return item_lat;
    }

    public void setItem_lat(String item_lat) {
        this.item_lat = item_lat;
    }

    public String getItem_long() {
        return item_long;
    }

    public void setItem_long(String item_long) {
        this.item_long = item_long;
    }

    public String getItem_location() {
        return item_location;
    }

    public void setItem_location(String item_location) {
        this.item_location = item_location;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public String getItem_memo() {
        return item_memo;
    }

    public void setItem_memo(String item_memo) {
        this.item_memo = item_memo;
    }

    public int getItem_check() {
        return item_check;
    }

    public void setItem_check(int item_check) {
        this.item_check = item_check;
    }

    public String getItem_time() {
        return item_time;
    }

    public void setItem_time(String item_time) {
        this.item_time = item_time;
    }
}
