package com.linsh.lshapp.part.detail;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface PersonDetailContract {

    interface View extends BaseContract.BaseView {

        String getPersonId();

        void setData(Person person);

        void setData(PersonDetail personDetail);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

    }
}
