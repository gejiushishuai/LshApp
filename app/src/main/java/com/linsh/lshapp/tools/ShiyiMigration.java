package com.linsh.lshapp.tools;

import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshRegexUtils;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by linsh on 17/4/30.
 */
public class ShiyiMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
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
                        .transform(obj -> {
                            DynamicRealmObject personAlbum = realm.createObject("PersonAlbum", obj.get("id"));
                            String avatar = obj.get("avatar");
                            String avatarThumb = obj.get("avatarThumb");
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
        }
    }
}
