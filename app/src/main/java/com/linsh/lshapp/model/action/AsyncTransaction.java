package com.linsh.lshapp.model.action;

import io.realm.Realm;
import rx.Subscriber;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public abstract class AsyncTransaction<T> implements Realm.Transaction {

    private Subscriber<? super T> mSubscriber;

    @Override
    public void execute(Realm realm) {
        execute(realm, mSubscriber);
        mSubscriber.onCompleted();
    }

    protected abstract void execute(Realm realm, Subscriber<? super T> subscriber);

    public void setSubscriber(Subscriber<? super T> subscriber) {
        mSubscriber = subscriber;
    }
}
