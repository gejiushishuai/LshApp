package com.linsh.lshapp.tools;


import com.linsh.lshapp.model.action.AsyncTransaction;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class LshRxUtils {

    public static Observable<Void> getDoNothingObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
            }
        });
    }

    public static <T> Observable<T> getAsyncTransactionObservable(final Realm realm, final AsyncTransaction<T> transaction) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                transaction.setSubscriber(subscriber);
                realm.executeTransactionAsync(transaction,
                        new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                subscriber.onCompleted();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                subscriber.onError(error);
                            }
                        });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
