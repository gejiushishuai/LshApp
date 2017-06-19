package com.linsh.lshapp.mvp.import_contacts;

import com.github.tamir7.contacts.Contact;
import com.linsh.lshapp.base.BaseContract;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface ImportContactsContract {

    interface View extends BaseContract.BaseView {

        void setData(List<Contact> contacts);

        void removeCurrentItem();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void addContact(Contact contact);
    }
}
