package com.linsh.lshapp.tools;

import android.os.Environment;

import com.linsh.lshapp.common.LshConfig;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshFileUtils;

import java.io.File;

import io.realm.Realm;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class LshFileFactory {

    private static String mAppDir;

    /**
     * 获取应用文件夹，文件夹名称在Config中配置
     */
    public static String getAppDir() {
        if (mAppDir == null) {
            File appDir = new File(Environment.getExternalStorageDirectory(), LshConfig.appNameEn);
            makeDir(appDir);
            mAppDir = appDir.getAbsolutePath();
        }
        return mAppDir;
    }

    private static File getCacheDir() {
        return LshApplicationUtils.getContext().getExternalCacheDir();
    }

    public static File getRealmFile() {
        File file = new File(LshApplicationUtils.getContext().getFilesDir(), "realm/shiyi.realm");
        if (!file.exists()) {
            Realm realm = Realm.getDefaultInstance();
            realm.writeCopyTo(file);
        }
        return file;
    }

    public static File getUploadAvatarFile() {
        return new File(LshApplicationUtils.getContext().getExternalCacheDir(), "header.jpg");
    }

    /**
     * 获取log文件夹
     */
    public static String getLogDir() {
        File dir = new File(getAppDir(), "log");
        makeDir(dir);
        return dir.getAbsolutePath();
    }

    public static String getJsonImportDir() {
        File dir = new File(getAppDir(), "import/json");
        makeDir(dir);
        return dir.getAbsolutePath();
    }

    private static void makeDir(File dir) {
        LshFileUtils.makeDirs(dir);
    }
}
