package com.linsh.lshapp.mvp.edit_person;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.Group;
import com.linsh.lshapp.model.bean.Person;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface PersonEditContract {

    interface View extends BaseContract.BaseView {

        String getPersonId();

        void setData(Person person);

        String getPrimaryGroup();

        void setGroup(String groupName);

        void onPersonModified();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        List<Group> getGroups();

        void addGroup(String inputText);

        void savePerson(String group, String name, String desc, String sex);
    }
}
