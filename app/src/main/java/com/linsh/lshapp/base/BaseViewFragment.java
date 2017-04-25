package com.linsh.lshapp.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public abstract class BaseViewFragment<T extends BaseActivity, P extends BaseContract.BasePresenter> extends BaseFragment<T> implements BaseContract.BaseView {

    protected P mPresenter;

    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = initPresenter();
        mPresenter.attachView(this);
    }

    protected abstract P initPresenter();

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }
}
