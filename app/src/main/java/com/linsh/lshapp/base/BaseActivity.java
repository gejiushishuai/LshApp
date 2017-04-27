package com.linsh.lshapp.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.linsh.lshapp.tools.LshActivityStatusTool;
import com.linsh.lshutils.utils.LshKeyboardUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class BaseActivity extends AppCompatActivity {

    private LshActivityStatusTool mStatusTool = new LshActivityStatusTool();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        // 设置屏幕方向
        setScreenOrientation();
        // 初始化布局
        initView();

        mStatusTool.onCreate();
    }

    protected abstract int getLayout();

    protected abstract void initView();

    @IntDef({ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }

    private void setScreenOrientation() {
        setRequestedOrientation(getScreenOrientation());
    }

    @Orientation
    protected abstract int getScreenOrientation();

    @Override
    protected void onStart() {
        super.onStart();
        mStatusTool.onStart();
    }

    protected void onResume() {
        super.onResume();
        mStatusTool.isOnResumed();
    }

    protected void onPause() {
        super.onPause();
        mStatusTool.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStatusTool.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStatusTool.onDestroy();
        // 关闭键盘
        LshKeyboardUtils.clearFocusAndHideKeyboard(this);
    }

    public LshActivityStatusTool getStatus() {
        return mStatusTool;
    }

    public boolean isOnDestroyed() {
        return mStatusTool.isOnDestroyed();
    }

    protected BaseActivity getActivity() {
        return this;
    }

}
