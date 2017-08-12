package com.tripco.www.tripco.model;

import java.io.Serializable;

/**
 * Created by kkmnb on 2017-08-12.
 */

public class AtoFModel implements Serializable {
    int trip_no;
    String start_date;
    String end_date;

    public AtoFModel(int trip_no, String start_date, String end_date) {
        this.trip_no = trip_no;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public int getTrip_no() {
        return trip_no;
    }

    public void setTrip_no(int trip_no) {
        this.trip_no = trip_no;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
