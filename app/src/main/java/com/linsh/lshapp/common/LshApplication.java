package com.linsh.lshapp.common;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/26
 *    desc   :
 * </pre>
 */
public class LshApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LshApplicationLike.init(this);
    }
}
