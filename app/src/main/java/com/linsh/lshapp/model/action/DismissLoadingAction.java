package com.linsh.lshapp.model.action;

import com.linsh.lshapp.base.BaseContract;

import rx.functions.Action1;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class DismissLoadingAction<T> implements Action1<T> {

    private final BaseContract.BaseView mBaseView;

    public DismissLoadingAction(BaseContract.BaseView baseView) {
        mBaseView = baseView;
    }

    @Override
    public void call(T t) {
        mBaseView.dismissLoadingDialog();
    }
}
