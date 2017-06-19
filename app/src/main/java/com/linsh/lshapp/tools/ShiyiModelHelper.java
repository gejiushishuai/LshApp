package com.linsh.lshapp.tools;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Senh Linsh on 17/4/27.
 */

public class ShiyiModelHelper {

    private static final String ID_REGEX = "^.+_\\d{12}$";
    public static final String UNNAME_GROUP_NAME = "未分组";

    public static String getId(String name) {
        return LshIdTools.getPinYinId(name);
    }

    public static String getIdWithTimeSuffix(String name) {
        return LshIdTools.getPinYinId(name) + getTimeSuffix();
    }

    public static String getTimeSuffix() {
        return new SimpleDateFormat("_yyMMddHHmmss").format(new Date());
    }

    public static String getGroupId(String groupName) {
        return getIdWithTimeSuffix(groupName);
    }

    public static String getPersonId(String personName) {
        return getIdWithTimeSuffix(personName);
    }

    public static String getTypeId(String personId, String typeName) {
        if (personId.matches(ID_REGEX)) {
            personId = personId.substring(0, personId.length() - 13);
            personId += "_";
            personId += getIdWithTimeSuffix(typeName);
            return personId;
        } else {
            Log.e("LshLog", "getTypeId: 格式异常!!!  personId = " + personId);
            return personId + typeName;
        }
    }

    public static String getTypeDetailId(String typeId) {
        if (typeId.matches(ID_REGEX)) {
            typeId = typeId.substring(0, typeId.length() - 13);
            typeId += getTimeSuffix();
            return typeId;
        } else {
            Log.e("LshLog", "getTypeDetailId: 格式异常!!!  typeId = " + typeId);
            return typeId + getTimeSuffix();
        }
    }

    public static String removeTimeSuffix(String str) {
        return str.replaceAll("_\\d{12}", "");
    }
}
