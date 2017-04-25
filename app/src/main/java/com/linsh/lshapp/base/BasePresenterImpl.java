package com.linsh.lshapp.base;

import io.realm.Realm;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public abstract class BasePresenterImpl<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {

    protected T mView;
    protected CompositeSubscription mCompositeDisposable;

    protected Realm mRealm;

    @Override
    public void attachView(T view) {
        mView = view;
        mCompositeDisposable = new CompositeSubscription();

        mRealm = Realm.getDefaultInstance();

        attachView();
    }

    @Override
    public void detachView() {
    }

    protected abstract void attachView();

    @Override
    public void subscribe() {
        if (mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
        mRealm.close();
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

    protected Realm getRealm() {
        return mRealm;
    }
}
