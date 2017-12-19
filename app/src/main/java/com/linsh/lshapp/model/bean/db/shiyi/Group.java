package com.linsh.lshapp.model.bean.db.shiyi;

import com.linsh.lshapp.model.bean.Typable;
import com.linsh.lshapp.tools.ShiyiModelHelper;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Senh Linsh on 17/1/22.
 * <p>
 * 组别
 * 用于给联系人进行分组
 */

public class Group extends RealmObject implements Typable {
    @PrimaryKey
    private String id;
    private String name;
    private int sort;
    private RealmList<Person> persons;

    public Group() {
    }

    public Group(String name, int sort) {
        this.id = ShiyiModelHelper.getGroupId(name);
        this.name = name;
        this.sort = sort;
        this.persons = new RealmList<>();
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

    public RealmList<Person> getPersons() {
        return persons;
    }

    public void setPersons(RealmList<Person> persons) {
        this.persons = persons;
    }

}
