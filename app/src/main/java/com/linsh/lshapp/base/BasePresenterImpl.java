package com.linsh.lshapp.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public abstract class BasePresenterImpl<T extends BaseView> implements BasePresenter<T> {

    protected BaseView mView;
    protected CompositeDisposable mCompositeDisposable;

    protected Realm realm;

    @Override
    public void attachView(T view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();

        attachView();
    }

    protected abstract void attachView();

    @Override
    public void subscribe() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
        realm.close();
    }

    protected BaseView getView() {
        return mView;
    }

    protected CompositeDisposable getDisposable() {
        return mCompositeDisposable;
    }

    protected void addDisposable(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }
}
