package com.transport.organelles.transport_.classforms;

/**
 * Created by Organelles on 9/22/2017.
 */

public class Withholding {

    String withholding_tag;
    String withholding_typeId;
    String withholding_typename;
    String withholding_amount;

    public Withholding(String withholding_tag, String withholding_typeId, String withholding_typename, String withholding_amount) {
        this.withholding_tag = withholding_tag;
        this.withholding_typeId = withholding_typeId;
        this.withholding_typename = withholding_typename;
        this.withholding_amount = withholding_amount;
    }


    public String getWithholding_tag() {
        return withholding_tag;
    }

    public void setWithholding_tag(String withholding_tag) {
        this.withholding_tag = withholding_tag;
    }

    public String getWithholding_typename() {
        return withholding_typename;
    }

    public void setWithholding_typename(String withholding_typename) {
        this.withholding_typename = withholding_typename;
    }

    public String getWithholding_amount() {
        return withholding_amount;
    }

    public void setWithholding_amount(String withholding_amount) {
        this.withholding_amount = withholding_amount;
    }
}
