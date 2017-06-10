package com.linsh.lshapp.model.bean.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Senh Linsh on 17/1/22.
 * <p>
 * 联系人详情
 * 保存该联系人的所有类型信息
 */
public class PersonDetail extends RealmObject {
    @PrimaryKey
    private String id;
    private RealmList<Type> types;

    public PersonDetail() {
    }

    public PersonDetail(String personId) {
        this.id = personId;
        this.types = new RealmList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<Type> getTypes() {
        return types;
    }

    public void setTypes(RealmList<Type> types) {
        this.types = types;
    }

}
