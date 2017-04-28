package com.linsh.lshapp.model.bean;

import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/1/22.
 */
public class Person extends RealmObject {
    private String id;
    private String name;
    private String describe;
    private String avatar;
    private int gender;

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

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
}
