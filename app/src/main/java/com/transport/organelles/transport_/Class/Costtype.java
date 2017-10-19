package com.transport.organelles.transport_.Class;

/**
 * Created by Organelles on 9/20/2017.
 */

public class Costtype {

    String costtypeid;
    String costtype;
    String amount;

    public Costtype() {

    }


    public Costtype( String costtype, String amount) {

        this.costtype = costtype;
        this.amount = amount;
    }


//    public String getCosttypeid() {
//        return costtypeid;
//    }
//
//    public void setCosttypeid(String costtypeid) {
//        this.costtypeid = costtypeid;
//    }
//
    public String getCosttype() {
        return costtype;
    }

    public void setCosttype(String costtype) {
        this.costtype = costtype;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
