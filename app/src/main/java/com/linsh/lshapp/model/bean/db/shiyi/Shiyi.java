package com.linsh.lshapp.model.bean.db.shiyi;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/1/22.
 * <p>
 * 拾意
 * 封装拾意相关数据
 */
public class Shiyi extends RealmObject {

    private RealmList<Group> groups;

    public Shiyi() {
        groups = new RealmList<>();
    }

    public RealmList<Group> getGroups() {
        return groups;
    }

    public void setGroups(RealmList<Group> groups) {
        this.groups = groups;
    }
}
