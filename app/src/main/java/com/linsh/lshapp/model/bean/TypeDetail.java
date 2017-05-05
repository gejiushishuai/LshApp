package com.linsh.lshapp.model.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/1/22.
 */
public class TypeDetail extends RealmObject {

    private String id;
    private int sort;
    private String detail;
    private String describe;
    private long timestamp;

    public TypeDetail() {
        refreshTimestamp();
    }

    public TypeDetail(String id, int sort, String detail, String describe) {
        this.id = id;
        this.sort = sort;
        this.detail = detail;
        this.describe = describe;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getStringTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(timestamp));
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void refreshTimestamp() {
        this.timestamp = System.currentTimeMillis();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
