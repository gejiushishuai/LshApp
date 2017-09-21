package com.linsh.lshapp.tools;

import android.util.Log;

import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshRegexUtils;

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
                            if (!LshStringUtils.isEmpty(avatar) && LshRegexUtils.isURL(avatar)) {
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
        }
    }
}
