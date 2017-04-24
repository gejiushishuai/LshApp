package com.linsh.lshapp.base;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public class BasePresenterImpl<T extends BaseView> implements BasePresenter<T> {

    private BaseView mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void attachView(T view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

}
