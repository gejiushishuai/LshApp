package com.linsh.lshapp.tools;


import rx.Observable;
import rx.Subscriber;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class RxUtils {

    public static Observable<Void> getDoNothingObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
            }
        });
    }
}
