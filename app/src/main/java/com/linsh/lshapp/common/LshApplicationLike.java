package com.linsh.lshapp.common;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.linsh.lshapp.lib.tinker.Log.MyLogImp;
import com.linsh.lshapp.lib.tinker.util.TinkerManager;
import com.linsh.lshapp.tools.LshTinkerReporter;
import com.linsh.lshapp.tools.RealmTool;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

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
        LshApplicationUtils.init(getApplication());
        // 初始化数据库
        RealmTool.init(getApplication());
    }
}
