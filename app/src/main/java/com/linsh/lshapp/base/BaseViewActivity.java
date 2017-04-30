package com.linsh.lshapp.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.linsh.lshapp.R;
import com.linsh.lshapp.view.ShapeLoadingDialog;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.Basic.LshToastUtils;
import com.linsh.lshutils.utils.LshRecourseUtils;
import com.linsh.lshutils.utils.LshSystemUtils;
import com.linsh.lshutils.view.LshColorDialog;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public abstract class BaseViewActivity<T extends BaseContract.BasePresenter> extends BaseActivity implements BaseContract.BaseView {

    protected T mPresenter;

    protected LshColorDialog mLshColorDialog;
    protected ShapeLoadingDialog mShapeLoadingDialog;

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
    public void showTextDialog(String content) {
        showTextDialog(null, content);
    }

    @Override
    public void showTextDialog(String title, String content) {
        mLshColorDialog = getTextDialog(title, content, null, null, null, null, false).show();
    }

    @Override
    public void showTextDialog(String content, LshColorDialog.OnPositiveListener onPositiveListener) {
        showTextDialog(content, null, onPositiveListener);
    }

    @Override
    public void showTextDialog(String content, String positiveBtn, LshColorDialog.OnPositiveListener onPositiveListener) {
        mLshColorDialog = getTextDialog(null, content, positiveBtn, onPositiveListener, null, null, false).show();
    }

    @Override
    public void showTextDialog(String content, LshColorDialog.OnPositiveListener onPositiveListener, LshColorDialog.OnNegativeListener onNegativeListener) {
        showTextDialog(content, null, onPositiveListener, null, onNegativeListener);
    }

    @Override
    public void showTextDialog(String content, String positiveBtn, LshColorDialog.OnPositiveListener onPositiveListener, String negativeBtn, LshColorDialog.OnNegativeListener onNegativeListener) {
        mLshColorDialog = getTextDialog(null, content, positiveBtn, onPositiveListener, negativeBtn, onNegativeListener, true).show();
    }

    public LshColorDialog.TextDialogBuilder getTextDialog(String content, String positive, LshColorDialog.OnPositiveListener
            onPositiveListener, String negative, LshColorDialog.OnNegativeListener onNegativeListener) {
        return getTextDialog(null, content, positive, onPositiveListener, negative, onNegativeListener, false);
    }

    public LshColorDialog.TextDialogBuilder getTextDialog(String title, String content, String positive, LshColorDialog.OnPositiveListener
            onPositiveListener, String negative, LshColorDialog.OnNegativeListener onNegativeListener, boolean defaultNegativeBtn) {
        if (mLshColorDialog != null && mLshColorDialog.isShowing()) {
            mLshColorDialog.dismiss();
        }
        LshColorDialog.TextDialogBuilder dialogBuilder =
                new LshColorDialog(getActivity())
                        .buildText()
                        .setTitle(LshStringUtils.isEmpty(title) ? "提示" : title)
                        .setContent(content)
                        .setBgColor(getResources().getColor(R.color.color_theme_dark_blue));

        dialogBuilder.setPositiveButton(positive == null ? "确定" : positive, onPositiveListener);

        if (onNegativeListener != null || defaultNegativeBtn)
            dialogBuilder.setNegativeButton(negative == null ? "取消" : negative, onNegativeListener);

        return dialogBuilder;
    }

    @Override
    public void showLoadingDialog() {
        showLoadingDialog(null, null);
    }

    @Override
    public void showLoadingDialog(String content) {
        showLoadingDialog(content, null);
    }

    @Override
    public void showLoadingDialog(String content, boolean cancelable) {
        showLoadingDialog(content, null);
        if (cancelable) {
            mShapeLoadingDialog.setCancelable(true);
        }
    }

    @Override
    public void showLoadingDialog(String content, DialogInterface.OnCancelListener cancelListener) {
        mShapeLoadingDialog = new ShapeLoadingDialog(this);
        mShapeLoadingDialog.setLoadingText(content == null ? "正在加载中..." : content);
        if (cancelListener != null) {
            mShapeLoadingDialog.setOnCancelListener(cancelListener);
        } else {
            mShapeLoadingDialog.setCancelable(false);
        }
        if (!isOnDestroyed()) mShapeLoadingDialog.show();
    }

    @Override
    public void dismissTextDialog() {
        if (!isOnDestroyed() && mLshColorDialog != null && mLshColorDialog.isShowing()) {
            mLshColorDialog.dismiss();
        }
    }

    @Override
    public void dismissLoadingDialog() {
        if (!isOnDestroyed() && mShapeLoadingDialog != null && mShapeLoadingDialog.getDialog().isShowing()) {
            mShapeLoadingDialog.dismiss();
        }
    }

    @Override
    public void showToast(String content) {
        LshToastUtils.showToast(content);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    protected int getScreenOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭弹出窗口
        if (mLshColorDialog != null && mLshColorDialog.isShowing()) {
            mLshColorDialog.dismiss();
        }
        if (mShapeLoadingDialog != null && mShapeLoadingDialog.getDialog().isShowing()) {
            mShapeLoadingDialog.dismiss();
        }
    }
}
