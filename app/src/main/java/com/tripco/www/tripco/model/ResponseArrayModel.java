package com.tripco.www.tripco.model;

import java.util.ArrayList;

public class ResponseArrayModel<T> {
    private int code;
    private String message;
    private ArrayList<T> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<T> getResult() {
        return result;
    }

    public void setResult(ArrayList<T> result) {
        this.result = result;
    }
}