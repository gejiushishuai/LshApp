package com.linsh.lshapp.tools;


import com.linsh.utilseverywhere.SharedPreferenceUtils;

/**
 * Created by Senh Linsh on 17/6/14.
 */

public class SharedPreferenceTools {

    // 上次备份 Realm 的时间
    private static final String LAST_BACKUP_REALM_TIME = "last_backup_realm_time";
    private static final String LAST_SIGN_IN_TIME = "sign_in_";


    public static long getLastBackupRealmTime() {
        return SharedPreferenceUtils.getLong(LAST_BACKUP_REALM_TIME, 0);
    }

    public static void putLastBackupRealmTime(long time) {
        SharedPreferenceUtils.putLong(LAST_BACKUP_REALM_TIME, time);
    }

    public static void refreshLastBackupRealmTime() {
        SharedPreferenceUtils.putLong(LAST_BACKUP_REALM_TIME, System.currentTimeMillis());
    }

    public static long getClientLastSignInTime(String clientKey) {
        return SharedPreferenceUtils.getLong(LAST_SIGN_IN_TIME + clientKey, 0);
    }

    public static void refreshClientSignedIn(String clientKey) {
        SharedPreferenceUtils.putLong(LAST_SIGN_IN_TIME + clientKey, System.currentTimeMillis());
    }

    public static void refreshClientSignedIn(String clientKey, long time) {
        SharedPreferenceUtils.putLong(LAST_SIGN_IN_TIME + clientKey, time);
    }
}
