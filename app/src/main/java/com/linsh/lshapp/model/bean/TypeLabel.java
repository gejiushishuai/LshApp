package com.linsh.lshapp.model.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class TypeLabel extends RealmObject {
    @PrimaryKey
    private String name;
    private int sort;

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
