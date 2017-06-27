package com.linsh.lshapp.tools;

import android.content.Intent;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.action.HttpThrowableAction;
import com.linsh.lshapp.model.bean.http.UpdateInfo;
import com.linsh.lshapp.service.UpdateService;
import com.linsh.lshapp.task.network.UrlConnector;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.LshAppUtils;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Senh Linsh on 17/6/12.
 */

public class VersionChecker {

    public static Subscription checkUpdate(BaseContract.BaseView view) {
        return UrlConnector.checkUpdate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(httpInfo -> {
                    if (httpInfo.code == 0 && httpInfo.data != null && httpInfo.data.apk != null) {
                        UpdateInfo.ApkBean apk = httpInfo.data.apk;
                        if (apk.version != null) {
                            // 判断如果有新版本的 APK, 则安装 APK
                            if (apk.version.compareTo(LshAppUtils.getVersionName()) > 0) {
                                view.showTextDialog("存在新版本, 是否下载新版本?", "下载", dialog -> {
                                    dialog.dismiss();
                                    view.showToast("正在下载新版本...");

                                    Intent intent = new Intent(view.getContext(), UpdateService.class);
                                    intent.putExtra(UpdateService.EXTRA_URL, apk.url);
                                    view.getContext().startService(intent);
                                }, null, null);
                            } else {
                                // 如果没有新版本 APK, 则判断是否需要升级补丁
                                Map<String, UpdateInfo.ApkBean> patches = httpInfo.data.patchs;
                                UpdateInfo.ApkBean patch = patches.get(LshAppUtils.getVersionName());
                                if (patch != null && patch.version.compareTo(LshAppUtils.getVersionName()) > 0) {
                                    view.showToast("正在进行补丁升级...");
                                    UrlConnector.downloadPatch(patch.url)
                                            .subscribe(file -> {
                                                TinkerInstaller.onReceiveUpgradePatch(LshApplicationUtils.getContext(), file.getAbsolutePath());
                                            }, new HttpThrowableAction());
                                } else {
                                    view.showToast("暂无更新");
                                }
                            }
                        }
                    } else {
                        view.showToast("暂无更新");
                    }
                }, e -> {
                    String error = HttpErrorCatcher.dispatchError(e);
                    view.showToast(error);
                });
    }
}
