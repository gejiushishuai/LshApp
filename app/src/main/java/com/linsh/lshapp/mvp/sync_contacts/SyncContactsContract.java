package com.linsh.lshapp.mvp.sync_contacts;

import com.linsh.lshapp.base.BaseContract;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface SyncContactsContract {

    interface View extends BaseContract.BaseView {

        void setData(List<ContactMixer> contacts);

        void updateItem();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void onClickStatus(ContactMixer mixer);

        void refreshAvatar(ContactMixer mixer);
    }
}
