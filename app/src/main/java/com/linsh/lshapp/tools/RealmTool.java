package com.linsh.lshapp.tools;

import android.app.Application;

import com.linsh.lshapp.common.LshConfig;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Senh Linsh on 17/1/22.
 */

public class RealmTool {

    public static void init(Application application) {
        Realm.init(application);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("shiyi.realm")
                .encryptionKey(getEncryptionKey())
                .schemaVersion(3)
                .migration(new ShiyiMigration())
//                .modules(new Shiyi(), new PersonDetail())
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

}
