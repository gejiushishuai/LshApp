package com.linsh.lshapp.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/1/22.
 */
public class PersonDetail extends RealmObject {

    private String id;
    private RealmList<Type> types;

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
