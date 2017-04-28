package com.linsh.lshapp.model.bean;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/1/22.
 */
public class Shiyi extends RealmObject {

    private RealmList<Group> groups;

    public RealmList<Group> getGroups() {
        return groups;
    }

    public void setGroups(RealmList<Group> groups) {
        this.groups = groups;
    }
}
