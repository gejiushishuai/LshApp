package com.linsh.lshapp.tools;

import android.os.Environment;

import com.linsh.lshapp.common.LshConfig;
import com.linsh.lshutils.utils.Basic.LshFileUtils;

import java.io.File;

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

    /**
     * 获取log文件夹
     */
    public static String getLogDir() {
        File audioDir = new File(getAppDir(), "log");
        makeDir(audioDir);
        return audioDir.getAbsolutePath();
    }

    private static void makeDir(File dir) {
        LshFileUtils.makeDirs(dir);
    }
}
