package com.linsh.lshapp.tools;

import android.util.Log;

import com.linsh.utilseverywhere.RegexUtils;
import com.linsh.utilseverywhere.StringUtils;

import java.util.Collections;
import java.util.Comparator;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by linsh on 17/4/30.
 */
public class ShiyiMigration implements RealmMigration {

    private static final int VERSION = 10;

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.i("LshLog", "Shiyi 数据库更新 --- oldVersion = " + oldVersion + ",  newVersion = " + newVersion);
        RealmSchema schema = realm.getSchema();
        switch ((int) oldVersion) {
            case 1:
                schema.get("Type")
                        .addField("id", String.class);
                schema.get("TypeDetail")
                        .addField("id", String.class);
            case 2:
                schema.create("TypeLabel")
                        .addField("sort", int.class)
                        .addField("name", String.class);
            case 3:
                schema.get("Group").addPrimaryKey("id");
                schema.get("Person").addPrimaryKey("id");
                schema.get("PersonDetail").addPrimaryKey("id");
                schema.get("Type").addPrimaryKey("id");
                schema.get("TypeDetail").addPrimaryKey("id");
            case 4:
                schema.get("TypeLabel").addPrimaryKey("name");
                schema.get("Person").addField("avatarThumb", String.class);
            case 5:
                schema.create("ImageUrl")
                        .addField("url", String.class)
                        .addField("thumbUrl", String.class);
                schema.create("PersonAlbum")
                        .addField("id", String.class)
                        .addPrimaryKey("id")
                        .addRealmListField("pictures", schema.get("ImageUrl"))
                        .addRealmListField("avatars", schema.get("ImageUrl"));
                schema.get("Person")
                        .transform(person -> {
                            DynamicRealmObject personAlbum = realm.createObject("PersonAlbum", person.get("id"));
                            String avatar = person.get("avatar");
                            String avatarThumb = person.get("avatarThumb");
                            if (!StringUtils.isEmpty(avatar) && RegexUtils.isURL(avatar)) {
                                DynamicRealmObject imageUrl = realm.createObject("ImageUrl");
                                imageUrl.setString("url", avatar);
                                imageUrl.setString("thumbUrl", avatarThumb);
                                personAlbum.getList("avatars").add(imageUrl);
                            }
                        });
            case 6:
                schema.get("Person")
                        .addField("syncWithContacts", boolean.class);
            case 7:
                schema.get("Person")
                        .addField("pinyin", String.class)
                        .transform(person -> {
                            person.set("pinyin", ShiyiModelHelper.getId(person.get("name")));
                        });
                schema.get("Group")
                        .transform(group -> {
                            RealmList<DynamicRealmObject> persons = group.getList("persons");
                            Collections.sort(persons, new Comparator<DynamicRealmObject>() {
                                @Override
                                public int compare(DynamicRealmObject o1, DynamicRealmObject o2) {
                                    String pinyin1 = o1.get("pinyin");
                                    String pinyin2 = o2.get("pinyin");
                                    if (pinyin1 == null) return -1;
                                    if (pinyin2 == null) return 1;
                                    return pinyin1.compareTo(pinyin2);
                                }
                            });
                        });
                break;
            case 8:
                schema.create("Task")
                        .addField("id", int.class).addPrimaryKey("id")
                        .addField("title", String.class)
                        .addField("frequency", int.class)
                        .addField("unit", String.class)
                        .addField("startTime", long.class)
                        .addField("lastUpdateTime", long.class)
                        .addField("progress", int.class);
                schema.create("Record")
                        .addField("id", int.class)
                        .addField("state", int.class)
                        .addField("note", String.class)
                        .addField("timestamp", long.class);
                break;
            case 9:
                schema.create("WebAvatar")
                        .addField("thumbUrl", String.class).addPrimaryKey("thumbUrl");
                schema.create("AccountAvatar")
                        .addField("url", String.class).addPrimaryKey("url")
                        .addField("thumbUrl", String.class);
                schema.create("Website")
                        .addField("name", String.class).addPrimaryKey("name")
                        .addRealmObjectField("avatar", schema.get("WebAvatar"));
                schema.create("Account")
                        .addField("id", long.class).addPrimaryKey("id")
                        .addRealmObjectField("website", schema.get("Website"))
                        .addField("name", String.class)
                        .addRealmObjectField("avatar", schema.get("AccountAvatar"))
                        .addField("loginName", String.class)
                        .addRealmListField("loginAccounts", schema.get("Account"));
                break;
        }
    }
}
