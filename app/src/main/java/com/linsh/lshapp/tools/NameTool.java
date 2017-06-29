package com.linsh.lshapp.tools;

/**
 * Created by Senh Linsh on 17/6/29.
 */

public class NameTool {

    public static String getAvatarName(String personName) {
        return "avatar_" + ShiyiModelHelper.getPersonId(personName);
    }

    public static String getAvatarThumbName(String avatar) {
        return "thumb_" + avatar;
    }
}
