package com.linsh.lshapp.mvp.home;

import com.linsh.lshapp.base.BaseContract;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface MainContract {

    interface MainView extends BaseContract.BaseView {

    }

    interface MainPresenter extends BaseContract.BasePresenter<MainView> {

    }
}
