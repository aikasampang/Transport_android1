package com.transport.organelles.transport_.model;

/**
 * Created by Organelles on 1/24/2018.
 */

public class remainingpax  {

    String id;
    String datetime;
    String from;
    String to;



    public remainingpax(){
    }

    public remainingpax(String id, String datetime, String from, String to) {
        this.id = id;
        this.datetime = datetime;
        this.from = from;
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
