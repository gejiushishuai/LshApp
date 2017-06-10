package com.linsh.lshapp.model.bean.db;

import com.linsh.lshapp.model.bean.Typable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Senh Linsh on 17/5/2.
 * <p>
 * 类型标签
 * 用于通过标签来快速生成类型和管理类型
 */

public class TypeLabel extends RealmObject implements Typable {
    @PrimaryKey
    private String name;
    private int sort;

    public TypeLabel() {
    }

    public TypeLabel(String name, int sort) {
        this.name = name;
        this.sort = sort;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
