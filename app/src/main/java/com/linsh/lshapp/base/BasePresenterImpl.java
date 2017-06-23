package com.linsh.lshapp.base;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public abstract class BasePresenterImpl<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T>, RealmChangeListener<Realm> {

    protected T mView;
    protected CompositeSubscription mCompositeDisposable;
    protected CompositeSubscription RxBusSubscriptions;

    protected Realm mRealm;
    private boolean mUnsubscribed;
    private boolean onChangedWhenUnsubscribe;

    @Override
    public void attachView(T view) {
        mView = view;
        mCompositeDisposable = new CompositeSubscription();
        RxBusSubscriptions = new CompositeSubscription();

        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(this);

        attachView();
    }

    @Override
    public void detachView() {
        mCompositeDisposable.unsubscribe();
        RxBusSubscriptions.clear();
        RxBusSubscriptions.unsubscribe();
        mRealm.close();
    }

    @Override
    public void invalidateView() {
    }

    protected abstract void attachView();

    @Override
    public void subscribe() {
        mUnsubscribed = false;
        if (onChangedWhenUnsubscribe) {
            invalidateView();
        }
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
        mUnsubscribed = true;
        onChangedWhenUnsubscribe = false;
    }

    @Override
    public void onChange(Realm element) {
        if (!mUnsubscribed) {
            invalidateView();
        } else {
            onChangedWhenUnsubscribe = true;
        }
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

    protected Realm getRealm() {
        return mRealm;
    }
}
