package com.linsh.lshapp.model.action;

import com.linsh.lshapp.base.BaseContract;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class DismissLoadingThrowableAction extends DefaultThrowableAction {

    private final BaseContract.BaseView mBaseView;

    public DismissLoadingThrowableAction(BaseContract.BaseView baseView) {
        mBaseView = baseView;
    }

    @Override
    public void call(Throwable throwable) {
        super.call(throwable);
        mBaseView.dismissLoadingDialog();
        DefaultThrowableAction.showThrowableMsg(throwable);
    }
}
