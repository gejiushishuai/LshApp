package com.linsh.lshapp.part.home.shiyi;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.Group;

import io.realm.RealmList;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public interface ShiyiContract {


    interface View extends BaseContract.BaseView {

        void setData(RealmList<Group> groups);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

    }
}
