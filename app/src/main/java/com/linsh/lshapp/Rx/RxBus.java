package com.linsh.lshapp.Rx;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

public class RxBus {

    private static volatile RxBus defaultInstance;
    private final FlowableProcessor<Object> mBus;

    private RxBus() {
        mBus = PublishProcessor.create().toSerialized();
    }

    public static RxBus getDefault() {
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new RxBus();
                }
            }
        }
        return defaultInstance;
    }

    public void post(Object o) {
        mBus.onNext(o);
    }

    public <T> Flowable<T> toObservable(Class<T> eventType) {
        return mBus.ofType(eventType);
    }
}