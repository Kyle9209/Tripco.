package com.tripco.www.tripco.model;

import java.io.Serializable;

/**
 * Created by kkmnb on 2017-08-08.
 */

public class ScheduleModel implements Serializable {
    String user_id;
    int trip_no;
    int schedule_no;
    String schedule_date;
    String item_url;
    int cate_no;
    String item_lat;
    String item_long;
    String item_placeid;
    String item_title;
    String item_memo;
    int item_check;
    String item_time;

    // 서버용 보내는 그릇
    public ScheduleModel(String user_id, int trip_no, String schedule_date, String item_url, int cate_no, String item_lat, String item_long, String item_placeid, String item_title, String item_memo) {
        this.user_id = user_id;
        this.trip_no = trip_no;
        this.schedule_date = schedule_date;
        this.item_url = item_url;
        this.cate_no = cate_no;
        this.item_lat = item_lat;
        this.item_long = item_long;
        this.item_placeid = item_placeid;
        this.item_title = item_title;
        this.item_memo = item_memo;
    }

    // 로컬 디비용 그릇
    public ScheduleModel(int trip_no, int schedule_no, String schedule_date, String item_url, int cate_no, String item_lat, String item_long, String item_placeid, String item_title, String item_memo, int item_check, String item_time) {
        this.trip_no = trip_no;
        this.schedule_no = schedule_no;
        this.schedule_date = schedule_date;
        this.item_url = item_url;
        this.cate_no = cate_no;
        this.item_lat = item_lat;
        this.item_long = item_long;
        this.item_placeid = item_placeid;
        this.item_title = item_title;
        this.item_memo = item_memo;
        this.item_check = item_check;
        this.item_time = item_time;
    }

    // 상세저장 데이터 전달 용 그릇
    public ScheduleModel(String schedule_date, int cate_no, String item_lat, String item_long, String item_placeid, String item_title, String item_memo) {
        this.schedule_date = schedule_date;
        this.cate_no = cate_no;
        this.item_lat = item_lat;
        this.item_long = item_long;
        this.item_placeid = item_placeid;
        this.item_title = item_title;
        this.item_memo = item_memo;
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

    public int getCate_no() {
        return cate_no;
    }

    public void setCate_no(int cate_no) {
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

    public String getItem_placeid() {
        return item_placeid;
    }

    public void setItem_placeid(String item_placeid) {
        this.item_placeid = item_placeid;
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
