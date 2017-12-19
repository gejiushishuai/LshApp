package com.linsh.lshapp.common;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.linsh.utilseverywhere.Utils;
import com.linsh.utilseverywhere.ActivityLifecycleUtils;
import com.linsh.lshapp.lib.tinker.Log.MyLogImp;
import com.linsh.lshapp.lib.tinker.util.TinkerManager;
import com.linsh.lshapp.tools.CrashHandler;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshapp.tools.LshTinkerReporter;
import com.linsh.lshapp.tools.RealmTool;
import com.linsh.lshutils.handler.LshCrashHandler;
import com.linsh.lshutils.utils.FileManagerUtils;
import com.linsh.lshutils.utils.LogPrinterUtils;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.io.File;


/**
 * Created by Senh Linsh on 17/5/10.
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = "com.linsh.lshapp.LshApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)
public class LshApplicationLike extends DefaultApplicationLike {

    public LshApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // 初始化MultiDex必须先于初始化Tinker
        MultiDex.install(base);

        TinkerManager.setTinkerApplicationLike(this);
        TinkerManager.setUpgradeRetryEnable(true);
        TinkerInstaller.setLogIml(new MyLogImp());

        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());

        TinkerManager.setReporter(new LshTinkerReporter());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Application application = getApplication();
        Utils.init(application);
        FileManagerUtils.initAppDir(new File(LshFileFactory.getAppDir()));

        RealmTool.init(application); // 初始化数据库
        ActivityLifecycleUtils.init(application); // 初始化 Activity 生命周期管理
        LogPrinterUtils.setLogFilePath(LshFileFactory.getLogFile("lshlog.txt")); // 设置打印本地的 Log 位置
        LshCrashHandler.install(application, new CrashHandler());
    }
}
