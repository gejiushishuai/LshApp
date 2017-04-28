package com.linsh.lshapp.part.add_person;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.Group;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface PersonAddContract {

    interface View extends BaseContract.BaseView {

        void addPersonSuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        List<Group> getGroups();

        void addGroup(String inputText);

        void addPerson(String group, String name, String desc, String sex);
    }
}
