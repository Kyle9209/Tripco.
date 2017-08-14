package com.tripco.www.tripco.model;

import java.io.Serializable;

/**
 * Created by kkmnb on 2017-08-12.
 */

public class FtoFModel implements Serializable {
    int trip_no;
    String schedule_date;
    int cate_no;

    public FtoFModel(int trip_no, String schedule_date, int cate_no) {
        this.trip_no = trip_no;
        this.schedule_date = schedule_date;
        this.cate_no = cate_no;
    }

    public int getTrip_no() {
        return trip_no;
    }

    public void setTrip_no(int trip_no) {
        this.trip_no = trip_no;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public int getCate_no() {
        return cate_no;
    }

    public void setCate_no(int cate_no) {
        this.cate_no = cate_no;
    }
}
