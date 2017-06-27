package com.linsh.lshapp.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.linsh.lshapp.lib.qcloud.QcloudSignCreater;
import com.linsh.lshapp.task.network.UrlConnector;
import com.linsh.lshutils.tools.LshDownloadManager;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshActivityLifecycleUtils;
import com.linsh.lshutils.utils.LshAppUtils;

import java.io.File;

/**
 * Created by Senh Linsh on 17/6/27.
 */

public class UpdateService extends Service {

    public static final String EXTRA_URL = "extra_url";
    private LshDownloadManager mManager;
    private BroadcastReceiver mReceiver;
    private long mId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = new LshDownloadManager("download_new_version");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LshLogUtils.i("UpdateService - BroadcastReceiver - onReceive");
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == mId) {
                    installApk();
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LshLogUtils.i("onStartCommand");
        String url = intent.getStringExtra(EXTRA_URL);
        String fileName = UrlConnector.getFileNameFromUrl(url);
        mId = mManager.buildRequest(url, fileName)
                .addRequestHeader("Authorization", QcloudSignCreater.getDownLoadSign("file/apk/" + fileName))
                .download();
        float progress = mManager.getProgress();
        if (progress >= 1) {
            installApk();
        } else {
            mManager.registerCompleteReceiver(mReceiver);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void installApk() {
        LshLogUtils.i("installApk");
        File file = LshDownloadManager.getFileIfDownloaded(mId);
        if (file != null && file.exists() && file.getName().endsWith(".apk")) {
            LshAppUtils.installApk(LshActivityLifecycleUtils.getTopActivity(), file);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mManager.stopQuery();
        mManager.unregisterReceiver(mReceiver);
    }
}
