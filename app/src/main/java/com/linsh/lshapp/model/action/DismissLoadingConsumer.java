package com.linsh.lshapp.model.action;

import com.linsh.lshapp.base.BaseContract;

import io.reactivex.functions.Consumer;


/**
 * Created by Senh Linsh on 17/4/28.
 */

public class DismissLoadingConsumer<T> implements Consumer<T> {

    private final BaseContract.BaseView mBaseView;

    public DismissLoadingConsumer(BaseContract.BaseView baseView) {
        mBaseView = baseView;
    }

    @Override
    public void accept(T t) {
        mBaseView.dismissLoadingDialog();
    }
}
