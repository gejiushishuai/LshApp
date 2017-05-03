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

import static android.R.attr.id;

/**
 * Created by Senh Linsh on 17/4/27.
 */

public class ShiyiModelHelper {

    public static String getId(String name) {
        return LshIdTools.getPinYinId(name) + getIdTimeSuffix();
    }

    private static String getIdTimeSuffix() {
        return new SimpleDateFormat("_yyMMddHHmmss").format(new Date());
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

        RealmList<TypeDetail> typeDetails = new RealmList<>();
        typeDetails.add(newTypeDetail(1, id));

        Type type = new Type();
        type.setName(typeName);
        type.setId(id);
        type.setSort(sort);
        type.setTypeDetails(typeDetails);
        return type;
    }

    public static TypeDetail newTypeDetail(int sort, String typeId) {
        TypeDetail detail = new TypeDetail();
        detail.setSort(sort);
        detail.setDetail("");
        detail.setDescribe("");
        detail.setId(typeId + getIdTimeSuffix());
        return detail;
    }
}
