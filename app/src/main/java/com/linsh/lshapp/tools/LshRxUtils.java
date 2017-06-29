package com.linsh.lshapp.tools;


import com.linsh.lshapp.model.action.AsyncAction;
import com.linsh.lshapp.model.action.AsyncTransaction;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class LshRxUtils {

    public static <T> Observable<T> getDoNothingObservable() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(null);
                subscriber.onCompleted();
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

    public static <T> Observable<T> getAsyncObservable(final AsyncAction<T> action1) {
        return Observable.unsafeCreate(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                Realm bgRealm = Realm.getDefaultInstance();
                try {
                    action1.call(bgRealm, subscriber);
                } catch (final Throwable e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } finally {
                    bgRealm.close();
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }
}
