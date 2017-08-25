package com.tripco.www.tripco.model;

import java.util.ArrayList;

/**
 * Created by kkmnb on 2017-08-24.
 */

public class TripDataModel {
    int tripNo;
    ArrayList<String> dateList; // [yyyy-mm-dd] 날짜 리스트
    ArrayList<String> dateSpinnerList; // [n일차(yyyy.mm.dd)] 날짜 리스트

    public int getTripNo() {
        return tripNo;
    }

    public void setTripNo(int tripNo) {
        this.tripNo = tripNo;
    }

    public ArrayList<String> getDateList() {
        return dateList;
    }

    public void setDateList(ArrayList<String> dateList) {
        this.dateList = dateList;
    }

    public ArrayList<String> getDateSpinnerList() {
        return dateSpinnerList;
    }

    public void setDateSpinnerList(ArrayList<String> dateSpinnerList) {
        this.dateSpinnerList = dateSpinnerList;
    }
}
