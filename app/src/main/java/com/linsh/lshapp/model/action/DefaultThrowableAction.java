package com.linsh.lshapp.model.action;

import com.linsh.lshutils.utils.Basic.LshToastUtils;

import rx.functions.Action1;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class DefaultThrowableAction implements Action1<Throwable> {

    @Override
    public void call(Throwable throwable) {
        throwable.printStackTrace();
        LshToastUtils.showToast(throwable.getMessage());
    }
}
