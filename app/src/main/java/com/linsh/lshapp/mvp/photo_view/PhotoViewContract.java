package com.linsh.lshapp.mvp.photo_view;

import com.linsh.lshapp.base.BaseContract;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface PhotoViewContract {

    interface View extends BaseContract.BaseView {

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

    }
}
