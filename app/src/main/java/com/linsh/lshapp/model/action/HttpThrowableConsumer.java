package com.linsh.lshapp.model.action;

import com.linsh.utilseverywhere.ToastUtils;
import com.linsh.lshapp.tools.HttpErrorCatcher;

import io.reactivex.functions.Consumer;


/**
 * Created by Senh Linsh on 17/4/28.
 */

public class HttpThrowableConsumer implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) {
        showThrowableMsg(throwable);
    }

    public static void showThrowableMsg(Throwable throwable) {
        throwable.printStackTrace();
        ToastUtils.show(HttpErrorCatcher.dispatchError(throwable));
    }
}
