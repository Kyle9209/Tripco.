package com.tripco.www.tripco.model;

import java.io.Serializable;

public class TripModel<T> implements Serializable {
    int trip_no;
    String trip_title;
    String start_date;
    String end_date;
    String user_id;
    String partner_id;
    String hashtag;
    T trip_list;

    // 서버용 받는 그릇
    public TripModel(int trip_no, String trip_title, String start_date, String end_date, String user_id, String partner_id, String hashtag, T trip_list) {
        this.trip_no = trip_no;
        this.trip_title = trip_title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.user_id = user_id;
        this.partner_id = partner_id;
        this.hashtag = hashtag;
        this.trip_list = trip_list;
    }

    // 로컬&서버 디비에 보내는 그릇
    public TripModel(int trip_no, String trip_title, String start_date, String end_date, String user_id, String partner_id, String hashtag) {
        this.trip_no = trip_no;
        this.trip_title = trip_title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.user_id = user_id;
        this.partner_id = partner_id;
        this.hashtag = hashtag;
    }

    // 여행 생성할 때 보내는 그릇
    public TripModel(String trip_title, String start_date, String end_date, String user_id, String partner_id, String hashtag) {
        this.trip_title = trip_title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.user_id = user_id;
        this.partner_id = partner_id;
        this.hashtag = hashtag;
    }

    // 여행 수정할 때 보내는 그릇
    public TripModel(int trip_no, String trip_title, String start_date, String end_date, String hashtag) {
        this.trip_no = trip_no;
        this.trip_title = trip_title;
        this.start_date = start_date;
        this.end_date = end_date;
        this.hashtag = hashtag;
    }

    // 여행 삭제할 때 보내는 그릇
    public TripModel(int trip_no) {
        this.trip_no = trip_no;
    }

    // 파트너 찾을때 보내는 그릇
    public TripModel(String user_id, String partner_id) {
        this.user_id = user_id;
        this.partner_id = partner_id;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(String partner_id) {
        this.partner_id = partner_id;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public T getTrip_list() {
        return trip_list;
    }

    public void setTrip_list(T trip_list) {
        this.trip_list = trip_list;
    }
}
