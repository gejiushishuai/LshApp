package com.linsh.lshapp.tools;

import io.realm.DynamicRealm;
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
                break;
        }
    }
}
