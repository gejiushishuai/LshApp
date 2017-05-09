package com.linsh.lshapp.model.bean;

import com.linsh.lshapp.tools.ShiyiModelHelper;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Senh Linsh on 17/1/22.
 */
public class Type extends RealmObject implements Typable {
    @PrimaryKey
    private String id;
    private String name;
    private int sort;
    private RealmList<TypeDetail> typeDetails;

    public Type() {
    }

    public Type(String personId, String name, int sort) {
        this.id = ShiyiModelHelper.getTypeId(personId, name);
        this.name = name;
        this.sort = sort;
        typeDetails = new RealmList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
}
