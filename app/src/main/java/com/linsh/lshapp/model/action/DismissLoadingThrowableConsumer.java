package com.linsh.lshapp.model.action;

import com.linsh.lshapp.base.BaseContract;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class DismissLoadingThrowableConsumer extends DefaultThrowableConsumer {

    private final BaseContract.BaseView mBaseView;

    public DismissLoadingThrowableConsumer(BaseContract.BaseView baseView) {
        mBaseView = baseView;
    }

    @Override
    public void accept(Throwable throwable) {
        super.accept(throwable);
        mBaseView.dismissLoadingDialog();
        DefaultThrowableConsumer.showThrowableMsg(throwable);
    }
}
