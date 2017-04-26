package com.linsh.lshapp.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.linsh.lshapp.R;
import com.linsh.lshutils.utils.LshRecourseUtils;
import com.linsh.lshutils.utils.LshSystemUtils;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public abstract class BaseViewActivity<T extends BaseContract.BasePresenter> extends BaseActivity implements BaseContract.BaseView {

    protected T mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置沉浸状态栏
        LshSystemUtils.setTransparentStatusBar(this, LshRecourseUtils.getColor(R.color.color_theme_dark_blue_pressed));
        // 初始化Presenter
        mPresenter = initPresenter();
        mPresenter.attachView(this);
    }

    protected abstract T initPresenter();

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    protected int getScreenOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
    }
}
