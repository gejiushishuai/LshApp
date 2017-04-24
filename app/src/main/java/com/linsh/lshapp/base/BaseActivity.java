package com.linsh.lshapp.base;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.linsh.lshapp.R;
import com.linsh.lshapp.view.ShapeLoadingDialog;
import com.linsh.lshutils.utils.LshKeyboardUtils;
import com.linsh.lshutils.utils.LshSystemUtils;
import com.linsh.lshutils.view.LshColorDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class BaseActivity extends AppCompatActivity {
    protected LshColorDialog mLshColorDialog;
    protected ShapeLoadingDialog mShapeLoadingDialog;
    public boolean onCreated;
    public boolean onStarted;
    public boolean onResumed;
    public boolean onPaused;
    public boolean onStopped;
    public boolean onDestroyed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        // 设置屏幕方向
        setScreenOrientation();
        // 初始化布局
        initView();
        // 初始化数据
        initData();

        onCreated = true;
        onDestroyed = false;
    }

    protected abstract int getLayout();

    protected abstract void initView();

    protected abstract void initData();

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
        onStarted = true;
        onStopped = false;
    }

    protected void onResume() {
        super.onResume();
        onResumed = true;
        onPaused = false;
    }

    protected void onPause() {
        super.onPause();
        onResumed = false;
        onPaused = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        onStarted = false;
        onStopped = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onCreated = false;
        onDestroyed = true;
        // 关闭弹出窗口
        if (mLshColorDialog != null && mLshColorDialog.isShowing()) {
            mLshColorDialog.dismiss();
        }
        if (mShapeLoadingDialog != null && mShapeLoadingDialog.getDialog().isShowing()) {
            mShapeLoadingDialog.dismiss();
        }
        // 关闭键盘
        LshKeyboardUtils.clearFocusAndHideKeyboard(this);
    }

    protected BaseActivity getActivity() {
        return this;
    }

    protected void handlerMessage(int what) {
    }

    protected void handlerMessage(Message msg) {
    }

    protected void setDefaultStatusBarColor() {
        setStatusBarColor(getResources().getColor(R.color.color_theme_dark_blue));
    }

    /**
     * 沉浸式状态栏
     */
    protected void setStatusBarColor(int color) {
        LshSystemUtils.setStatusBarColor(this, color);
    }

    /**
     * 显示弹出窗口
     * 只有一个确定按钮,默认点击后消失
     */
    public LshColorDialog showTextDialog(String content) {
        return showTextDialog(content, new LshColorDialog.OnPositiveListener() {
            @Override
            public void onClick(LshColorDialog dialog1) {
                dialog1.dismiss();
            }
        }, null);
    }

    /**
     * 显示弹出窗口
     *
     * @param content                   提示内容
     * @param onPositiveListener        确定按钮的点击监听,为null则没有
     * @param defaultOnNegativeListener 是否使用默认的取消按钮点击监听, true 即点击后消失, false 即没有取消按钮
     */
    public LshColorDialog showTextDialog(String content, @Nullable LshColorDialog.OnPositiveListener
            onPositiveListener, boolean defaultOnNegativeListener) {
        if (defaultOnNegativeListener) {
            return showTextDialog(content, onPositiveListener, new LshColorDialog.OnNegativeListener() {
                @Override
                public void onClick(LshColorDialog dialog1) {
                    dialog1.dismiss();
                }
            });
        } else {
            return showTextDialog(content, onPositiveListener, null);
        }
    }

    /**
     * 显示弹出窗口
     *
     * @param content            提示内容
     * @param onPositiveListener 确定按钮的点击监听,为null则没有
     * @param onNegativeListener 取消按钮的点击监听,为null则没有
     */
    public LshColorDialog showTextDialog(String content, @Nullable LshColorDialog.OnPositiveListener
            onPositiveListener, @Nullable LshColorDialog.OnNegativeListener onNegativeListener) {
        if (!onDestroyed) return getTextDialog(content, onPositiveListener, onNegativeListener).show();
        return mLshColorDialog;
    }

    public LshColorDialog showTextDialog(String content, @Nullable String positive, @Nullable LshColorDialog.OnPositiveListener
            onPositiveListener, @Nullable String negative, @Nullable LshColorDialog.OnNegativeListener onNegativeListener) {
        if (!onDestroyed)
            return getTextDialog(content, positive, onPositiveListener, negative, onNegativeListener).show();
        return mLshColorDialog;
    }

    /**
     * 获取dialog的对象, 需要自己手动调用 show()
     *
     * @param content            提示内容
     * @param onPositiveListener 确定按钮的点击监听,为null则没有
     * @param onNegativeListener 取消按钮的点击监听,为null则没有
     */
    public LshColorDialog.TextDialogBuilder getTextDialog(String content, @Nullable LshColorDialog.OnPositiveListener
            onPositiveListener, @Nullable LshColorDialog.OnNegativeListener onNegativeListener) {
        return getTextDialog(content, null, onPositiveListener, null, onNegativeListener);
    }

    public LshColorDialog.TextDialogBuilder getTextDialog(String content, @Nullable String positive, @Nullable LshColorDialog.OnPositiveListener
            onPositiveListener, @Nullable String negative, @Nullable LshColorDialog.OnNegativeListener onNegativeListener) {
        if (mLshColorDialog != null && mLshColorDialog.isShowing()) {
            mLshColorDialog.dismiss();
        }
        LshColorDialog.TextDialogBuilder dialogBuilder =
                new LshColorDialog(BaseActivity.this)
                        .buildText()
                        .setTitle("提示")
                        .setContent(content)
                        .setBgColor(getResources().getColor(R.color.color_theme_dark_blue));

        if (onPositiveListener != null)
            dialogBuilder.setPositiveButton(positive == null ? "确定" : positive, onPositiveListener);
        if (onNegativeListener != null)
            dialogBuilder.setNegativeButton(negative == null ? "取消" : negative, onNegativeListener);

        return dialogBuilder;
    }

    /**
     * 取消提示窗口
     */
    public void dismissLshColorDialog() {
        if (!onDestroyed && mLshColorDialog != null && mLshColorDialog.isShowing()) {
            mLshColorDialog.dismiss();
        }
    }

    /**
     * 显示Loading窗口
     * 默认文字: "正在加载中..."
     */
    public ShapeLoadingDialog showLoadingDialog() {
        return showLoadingDialog(null, null);
    }

    /**
     * 显示Loading窗口
     *
     * @param content 提示内容, 如果为null 则显示默认文字: "正在加载中..."
     */
    public ShapeLoadingDialog showLoadingDialog(@Nullable String content) {
        return showLoadingDialog(content, null);
    }

    /**
     * 显示Loading窗口
     *
     * @param content 提示内容, 如果为null 则显示默认文字: "正在加载中..."
     */
    public ShapeLoadingDialog showLoadingDialog(@Nullable String content, boolean cancelable) {
        ShapeLoadingDialog shapeLoadingDialog = showLoadingDialog(content, null);
        if (cancelable) {
            shapeLoadingDialog.setCancelable(true);
            return shapeLoadingDialog;
        }
        return shapeLoadingDialog;
    }

    /**
     * 显示Loading窗口
     *
     * @param content        提示内容, 如果为null 则显示默认文字: "正在加载中..."
     * @param cancelListener 按返回键取消Loading的监听
     */
    public ShapeLoadingDialog showLoadingDialog(@Nullable String content, DialogInterface.OnCancelListener cancelListener) {
        mShapeLoadingDialog = new ShapeLoadingDialog(this);
        mShapeLoadingDialog.setLoadingText(content == null ? "正在加载中..." : content);
        if (cancelListener != null) {
            mShapeLoadingDialog.setOnCancelListener(cancelListener);
        } else {
            mShapeLoadingDialog.setCancelable(false);
        }
        if (!onDestroyed) mShapeLoadingDialog.show();
        return mShapeLoadingDialog;
    }

    /**
     * 取消Loading
     */
    public void dismissLoadingDialog() {
        if (!onDestroyed && mShapeLoadingDialog != null && mShapeLoadingDialog.getDialog().isShowing()) {
            mShapeLoadingDialog.dismiss();
        }
    }
}
