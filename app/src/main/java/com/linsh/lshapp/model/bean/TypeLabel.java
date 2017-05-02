package com.linsh.lshapp.model.bean;

import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class TypeLabel extends RealmObject {

    private int sort;
    private String name;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
