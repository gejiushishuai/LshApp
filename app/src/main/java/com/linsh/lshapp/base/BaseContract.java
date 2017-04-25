package com.linsh.lshapp.base;

import android.content.Context;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface BaseContract {

    interface BaseView {

        Context getContext();

        //    void showTextDialog(String msg);
        //
        //    void showTextDialog(String title, String msg);
        //
        //    void showTextDialog(String msg, LshColorDialog.OnPositiveListener onPositiveListener);
        //
        //    void showTextDialog(String msg, String positiveBtn, LshColorDialog.OnPositiveListener onPositiveListener);
        //
        //    void showTextDialog(String msg, LshColorDialog.OnPositiveListener onPositiveListener, LshColorDialog.OnNegativeListener onNegativeListener);
        //
        //    void showTextDialog(String msg, String positiveBtn, LshColorDialog.OnPositiveListener onPositiveListener,
        //                        String negativeBtn, LshColorDialog.OnNegativeListener onNegativeListener);
        //
        //    void showLoadingDialog(String msg);
        //
        //    void showLoadingDialog(String msg, boolean cancelable);
        //
        //    void showLoadingDialog(String msg, DialogInterface.OnCancelListener cancelListener);
        //
        //    void showToast(String msg);
    }

    interface BasePresenter<T extends BaseView> {

        void attachView(T view);

        void detachView();

        void subscribe();

        void unsubscribe();
    }
}
