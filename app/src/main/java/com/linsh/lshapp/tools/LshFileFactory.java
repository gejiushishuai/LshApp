package com.linsh.lshapp.tools;

import android.os.Environment;

import com.linsh.lshapp.common.LshConfig;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshFileUtils;
import com.linsh.lshutils.utils.Basic.LshIOUtils;

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
        File cacheDir = LshApplicationUtils.getContext().getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = LshApplicationUtils.getContext().getCacheDir();
        }
        return cacheDir;
    }

    private static File getFileAir() {
        File filesDir = LshApplicationUtils.getContext().getExternalFilesDir(null);
        if (filesDir == null) {
            filesDir = LshApplicationUtils.getContext().getFilesDir();
        }
        return filesDir;
    }

    private static File getOutputDir() {
        File dir = new File(getAppDir(), "export");
        makeDir(dir);
        return dir;
    }

    public static File getRealmFile() {
        File file = new File(LshApplicationUtils.getContext().getFilesDir(), "realm/shiyi.realm");
        if (file.exists()) {
            if (!file.delete()) {
                return file;
            }
        } else {
            LshFileUtils.makeParentDirs(file);
        }
        Realm realm = Realm.getDefaultInstance();
        realm.writeCopyTo(file);
        LshIOUtils.close(realm);
        return file;
    }

    public static File getUploadAvatarFile(String id) {
        return new File(LshApplicationUtils.getContext().getExternalCacheDir(), "avatar_" + id + ".jpg");
    }

    public static File getUploadThumbFile(String id) {
        return new File(LshApplicationUtils.getContext().getExternalCacheDir(), "thumb_" + id + ".jpg");
    }

    /**
     * 获取log文件夹
     */
    public static String getLogFile(String fileName) {
        File file = new File(getAppDir(), "log/" + fileName);
        LshFileUtils.makeParentDirs(file);
        return file.getAbsolutePath();
    }

    public static String getJsonImportDir() {
        File dir = new File(getAppDir(), "import/json");
        makeDir(dir);
        return dir.getAbsolutePath();
    }

    private static void makeDir(File dir) {
        LshFileUtils.makeDirs(dir);
    }

    public static File getApkDir() {
        File dir = new File(getFileAir(), "apk");
        makeDir(dir);
        return dir;
    }

    public static File getPatchFile(String fileName) {
        File dir = new File(getFileAir(), "patch");
        makeDir(dir);
        return new File(dir, fileName);
    }

    public static File getOutputWordRepoFile() {
        return new File(getOutputDir(), "联系人词库.txt");
    }
}
