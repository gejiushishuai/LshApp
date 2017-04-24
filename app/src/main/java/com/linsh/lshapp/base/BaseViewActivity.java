package com.linsh.lshapp.base;

import android.content.Context;
import android.content.pm.ActivityInfo;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public abstract class BaseViewActivity extends BaseActivity implements BaseView {

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    protected int getScreenOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
    }
}
