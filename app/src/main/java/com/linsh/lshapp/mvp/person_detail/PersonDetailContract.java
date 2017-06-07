package com.linsh.lshapp.mvp.person_detail;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.TypeLabel;

import java.util.List;

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

        List<TypeLabel> getTypeLabels();

        void addTypeLabel(String inputText);

        void addType(String typeName);

        void addType(String typeName, int sort);

        void deleteType(String typeId);

        void deleteTypeDetail(String typeDetailId);

        void deletePerson();

        PersonDetail getPersonDetail();

        Person getPerson();
    }
}
