package com.tripco.www.tripco.model;

public class TripModel {
    int trip_no;
    String trip_title;
    String start_date;
    String end_date;
    int user_no;
    int partner_no;
    String hashtag;

    public TripModel(int trip_no, String trip_title, String start_date, String end_date, int user_no, int partner_no, String hashtag) {
        this.trip_no = trip_no;
        this.trip_title = trip_title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.user_no = user_no;
        this.partner_no = partner_no;
        this.hashtag = hashtag;
    }

    public int getTrip_no() {
        return trip_no;
    }

    public void setTrip_no(int trip_no) {
        this.trip_no = trip_no;
    }

    public String getTrip_title() {
        return trip_title;
    }

    public void setTrip_title(String trip_title) {
        this.trip_title = trip_title;
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

    public int getUser_no() {
        return user_no;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    public int getPartner_no() {
        return partner_no;
    }

    public void setPartner_no(int partner_no) {
        this.partner_no = partner_no;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }
}
