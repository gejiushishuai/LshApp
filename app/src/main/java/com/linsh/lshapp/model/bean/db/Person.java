package com.linsh.lshapp.model.bean.db;

import com.linsh.lshapp.tools.ShiyiModelHelper;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Senh Linsh on 17/1/22.
 * <p>
 * 联系人
 * 用于保存联系人基本信息, 详细类型信息需要使用 id 通过 PersonDetail 进行查找
 */
public class Person extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String describe;
    private String avatar;
    private String avatarThumb;
    private int gender;
    private boolean syncWithContacts;

    public Person() {
    }

    public Person(String name) {
        this(name, "", "", "", "", false);
    }

    public Person(String name, String describe, int gender) {
        this.id = ShiyiModelHelper.getPersonId(name);
        this.name = name;
        this.describe = describe;
        this.gender = gender;
    }

    public Person(String name, String describe, String avatar, String avatarThumb, String gender, boolean sync) {
        this.id = ShiyiModelHelper.getPersonId(name);
        this.name = name;
        this.describe = describe;
        setAvatar(avatar, avatarThumb);
        setGender(gender);
        syncWithContacts = sync;
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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAvatarThumb() {
        return avatarThumb;
    }

    public void setAvatar(String avatar, String avatarThumb) {
        this.avatar = avatar;
        this.avatarThumb = avatarThumb;
    }

    public String getGender() {
        String gender = "";
        switch (this.gender) {
            case 1:
                gender = "男";
                break;
            case 2:
                gender = "女";
                break;
        }
        return gender;
    }

    public int getIntGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setGender(String gender) {
        if ("男".equals(gender)) {
            this.gender = 1;
        } else if ("女".equals(gender)) {
            this.gender = 2;
        } else {
            this.gender = 0;
        }
    }

    public boolean isSyncWithContacts() {
        return syncWithContacts;
    }

    public void setSyncWithContacts(boolean syncWithContacts) {
        this.syncWithContacts = syncWithContacts;
    }
}
