package com.linsh.lshapp.common;

import android.app.Application;

import com.linsh.lshapp.tools.RealmTool;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public class LshApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LshApplicationUtils.init(this);
        // 初始化数据库
        RealmTool.init(this);
    }
}
