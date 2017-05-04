package com.linsh.lshapp.model.action;

import rx.functions.Action1;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class NothingAction<T> implements Action1<T> {

    @Override
    public void call(T t) {
    }
}
