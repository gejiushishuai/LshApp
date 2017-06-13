package com.linsh.lshapp.mvp.setting;

import com.linsh.lshapp.base.BaseContract;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface SettingsContract {

    interface View extends BaseContract.BaseView {

    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void outputDatabase();

        void importGson();

        void checkUpdate();
    }
}
