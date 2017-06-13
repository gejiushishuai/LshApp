package com.linsh.lshapp.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.linsh.lshutils.tools.LshDownloadManager;
import com.linsh.lshutils.utils.LshActivityLifecycleUtils;
import com.linsh.lshutils.utils.LshAppUtils;

import java.io.File;

/**
 * Created by Senh Linsh on 17/6/12.
 */

public class InstallApkReceiver extends BroadcastReceiver {

    private long mId;

    public InstallApkReceiver(long id) {
        mId = id;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (id == mId) {
            File file = LshDownloadManager.getFileIfDownloaded(id);
            if (file != null && file.exists() && file.getName().endsWith(".apk")) {
                LshAppUtils.installApk(LshActivityLifecycleUtils.getTopActivity(), file);
            }
        }
    }

}
