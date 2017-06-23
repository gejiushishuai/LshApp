package com.linsh.lshapp.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public abstract class BasePresenterImpl<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {

    private T mView;
    private CompositeSubscription mCompositeDisposable;
    private CompositeSubscription RxBusSubscriptions;

    @Override
    public void attachView(T view) {
        mView = view;
        mCompositeDisposable = new CompositeSubscription();
        RxBusSubscriptions = new CompositeSubscription();

        attachView();
    }

    @Override
    public void detachView() {
        mCompositeDisposable.unsubscribe();
        RxBusSubscriptions.clear();
        RxBusSubscriptions.unsubscribe();
    }

    protected abstract void attachView();

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    protected T getView() {
        return mView;
    }

    protected CompositeSubscription getSubscription() {
        return mCompositeDisposable;
    }

    protected void addSubscription(Subscription disposable) {
        mCompositeDisposable.add(disposable);
    }

    protected void addSubscription(Subscription... disposables) {
        for (Subscription disposable : disposables) {
            mCompositeDisposable.add(disposable);
        }
    }

    protected void addRxBusSub(Subscription disposable) {
        RxBusSubscriptions.add(disposable);
    }

    protected void addRxBusSub(Subscription... disposables) {
        for (Subscription disposable : disposables) {
            RxBusSubscriptions.add(disposable);
        }
    }
}
