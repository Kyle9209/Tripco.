package com.tripco.www.tripco.model;

public class TripModel {
    String title;
    String who;
    String start;
    String end;
    String tag;

    public TripModel(String title, String who, String start, String end, String tag) {
        this.title = title;
        this.who = who;
        this.start = start;
        this.end = end;
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
