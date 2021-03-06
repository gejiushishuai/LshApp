package com.linsh.lshapp.tools;

import android.app.Application;
import android.util.Log;

import com.linsh.utilseverywhere.IOUtils;
import com.linsh.lshapp.common.LshConfig;
import com.linsh.lshapp.common.LshConstants;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Senh Linsh on 17/1/22.
 */

public class RealmTool {

    public static void init(Application application) {
        Realm.init(application);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(LshConstants.NAME_REALM_FILE)
                .encryptionKey(getEncryptionKey())
                .schemaVersion(9)
                .migration(new ShiyiMigration())
                .build();
        // 设置默认配置
        Realm.setDefaultConfiguration(config);
    }

    public static byte[] getEncryptionKey() {
        byte[] bytes = LshConfig.password.getBytes();
        byte[] password = new byte[64];
        System.arraycopy(bytes, 0, password, 0, bytes.length);
        return password;
    }

    // 检查 Realm 数据库是否需要备份
    public static boolean checkBackupRealm() {
        long lastBackupRealmTime = SharedPreferenceTools.getLastBackupRealmTime();

        Realm realm = Realm.getDefaultInstance();
        File file = new File(realm.getPath());
        try {
            if (file.exists()) {
                long lastModified = file.lastModified();
                return lastModified > lastBackupRealmTime;
            } else {
                Log.i("LshLog", "file not exists");
            }
        } finally {
            IOUtils.close(realm);
        }
        return false;
    }
}
