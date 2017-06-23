package com.linsh.lshapp.mvp.search;

import com.linsh.lshapp.base.RealmPresenterImpl;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class SearchPresenter extends RealmPresenterImpl<SearchContract.View> implements SearchContract.Presenter {

    @Override
    protected void attachView() {
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void search(String query) {
    }
}
