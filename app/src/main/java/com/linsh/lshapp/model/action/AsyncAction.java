package com.linsh.lshapp.model.action;

import io.realm.Realm;
import rx.Subscriber;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public interface AsyncAction<T> {


    void call(Realm realm, Subscriber<? super T> subscriber);

}
