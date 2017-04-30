package com.linsh.lshapp.model.bean;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/1/22.
 */
public class Type extends RealmObject {

    private String id;
    private String name;
    private int sort;
    private RealmList<TypeDetail> typeDetails;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public RealmList<TypeDetail> getTypeDetails() {
        return typeDetails;
    }

    public void setTypeDetails(RealmList<TypeDetail> typeDetails) {
        this.typeDetails = typeDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
