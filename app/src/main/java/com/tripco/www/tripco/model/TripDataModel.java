package com.tripco.www.tripco.model;

import java.util.ArrayList;

/**
 * Created by kkmnb on 2017-08-24.
 */

public class TripDataModel {
    int tripNo;
    ArrayList<String> dateSpinnerList; // [n일차(yyyy.mm.dd)] 날짜 리스트

    public int getTripNo() {
        return tripNo;
    }

    public void setTripNo(int tripNo) {
        this.tripNo = tripNo;
    }

    public ArrayList<String> getDateSpinnerList() {
        return dateSpinnerList;
    }

    public void setDateSpinnerList(ArrayList<String> dateSpinnerList) {
        this.dateSpinnerList = dateSpinnerList;
    }
}
