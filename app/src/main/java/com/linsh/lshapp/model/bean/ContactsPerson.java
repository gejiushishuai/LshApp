package com.linsh.lshapp.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Senh Linsh on 17/8/22.
 */

public class ContactsPerson {

    private String id;
    private String name;
    private String avatar;
    private String avatarThumb;
    private String birthday;
    private String lunarBirthday;
    private List<String> phoneNumber;

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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLunarBirthday() {
        return lunarBirthday;
    }

    public void setLunarBirthday(String lunarBirthday) {
        this.lunarBirthday = lunarBirthday;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void addPhoneNumber(String phoneNumber) {
        if (this.phoneNumber == null) {
            this.phoneNumber = new ArrayList<>();
        }
        this.phoneNumber.add(phoneNumber);
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

}
