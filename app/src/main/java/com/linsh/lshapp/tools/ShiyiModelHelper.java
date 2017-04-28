package com.linsh.lshapp.tools;

import com.linsh.lshapp.model.bean.Group;
import com.linsh.lshapp.model.bean.Person;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmList;

/**
 * Created by Senh Linsh on 17/4/27.
 */

public class ShiyiModelHelper {

    public static String getId(String name) {
        return LshIdTools.getPinYinId(name) + new SimpleDateFormat("_yyMMddHHmmss").format(new Date());
    }

    public static Group newGroup(String name, int sort) {
        Group group = new Group();
        group.setPersons(new RealmList<Person>());
        group.setName(name);
        group.setId(getId(name));
        group.setSort(sort);
        return group;
    }

    public static Person newPerson(String name, String desc, String sex) {
        Person person = new Person();
        person.setName(name);
        person.setId(getId(name));
        person.setAvatar("");
        person.setDescribe(desc);
        person.setGender(sex);
        return person;
    }
}
