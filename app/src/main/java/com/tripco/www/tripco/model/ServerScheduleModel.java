package com.tripco.www.tripco.model;

import java.util.ArrayList;

/**
 * Created by kkmnb on 2017-08-27.
 */

public class ServerScheduleModel<T> {
    String schedule_date;
    ArrayList<T> schedule_list;

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public ArrayList<T> getSchedule_list() {
        return schedule_list;
    }

    public void setSchedule_list(ArrayList<T> schedule_list) {
        this.schedule_list = schedule_list;
    }
}
