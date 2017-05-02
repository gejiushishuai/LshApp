package com.linsh.lshapp.tools;

import com.linsh.lshapp.model.bean.Group;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.Type;
import com.linsh.lshapp.model.bean.TypeDetail;
import com.linsh.lshapp.model.bean.TypeLabel;

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

    public static TypeLabel newTypeLabel(int sort, String name) {
        TypeLabel typeLabel = new TypeLabel();
        typeLabel.setSort(sort);
        typeLabel.setName(name);
        return typeLabel;
    }

    public static PersonDetail newPersonDetail(String personId) {
        PersonDetail detail = new PersonDetail();
        detail.setId(personId);
        detail.setTypes(new RealmList<Type>());
        return detail;
    }

    public static Type newType(String personId, int sort, String typeName) {
        int endIndex = personId.length() - 12;
        String id = (endIndex > 0 ? personId.substring(0, endIndex) : personId) + "_" + typeName;

        Type type = new Type();
        type.setName(typeName);
        type.setId(id);
        type.setSort(sort);
        type.setTypeDetails(new RealmList<TypeDetail>());
        return type;
    }
}
