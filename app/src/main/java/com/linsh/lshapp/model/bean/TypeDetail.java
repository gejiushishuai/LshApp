package com.linsh.lshapp.model.bean;

import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/1/22.
 */
public class TypeDetail extends RealmObject {

    private int sort;
    private String detail;
    private String describe;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

}
