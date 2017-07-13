package com.linsh.lshapp.mvp.edit_person;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.Person;

import java.io.File;
import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface PersonEditContract {

    interface View extends BaseContract.BaseView {

        String getPersonId();

        void setData(Person person);

        void setGroup(String groupName);

        void onPersonModified();

        void showPersonDetail(String personId);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        List<Group> getGroups();

        void addGroup(String inputText);

        void savePerson(String group, String name, String desc, String sex, File avatarFile);

        void checkName(String name);
    }
}
