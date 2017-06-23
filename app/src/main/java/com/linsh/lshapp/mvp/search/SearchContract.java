package com.linsh.lshapp.mvp.search;

import com.linsh.lshapp.base.BaseContract;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface SearchContract {

    interface View extends BaseContract.BaseView {

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void search(String query);
    }
}
