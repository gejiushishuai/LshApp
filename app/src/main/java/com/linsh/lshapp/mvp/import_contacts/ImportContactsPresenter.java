package com.linsh.lshapp.mvp.import_contacts;

import com.github.tamir7.contacts.Address;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Event;
import com.github.tamir7.contacts.PhoneNumber;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.Type;
import com.linsh.lshapp.model.bean.db.TypeDetail;
import com.linsh.lshapp.model.throwabes.PersonRepeatThrowable;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.tools.ShiyiModelHelper;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshStringUtils;

import java.util.List;

import io.realm.RealmList;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class ImportContactsPresenter extends BasePresenterImpl<ImportContactsContract.View> implements ImportContactsContract.Presenter {

    @Override
    protected void attachView() {
        getView().showLoadingDialog();
        Observable<List<Contact>> observable = Observable.unsafeCreate(new Observable.OnSubscribe<List<Contact>>() {
            @Override
            public void call(Subscriber<? super List<Contact>> subscriber) {
                Contacts.initialize(LshApplicationUtils.getContext());
                subscriber.onNext(Contacts.getQuery().hasPhoneNumber().find());
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contacts -> {
                    getView().dismissLoadingDialog();
                    if (contacts.size() > 0) {
                        getView().setData(contacts);
                    } else {
                        getView().showTextDialog("手机通讯录中没有联系人哦");
                    }
                }, throwable -> {
                    getView().dismissLoadingDialog();
                    DefaultThrowableAction.showThrowableMsg(throwable);
                });
    }

    @Override
    public void addContact(Contact contact) {
        getView().showLoadingDialog();

        Person person = getPerson(contact);
        PersonDetail personDetail = getPersonDetail(contact, person.getId());
        Subscription addPersonAddDetailSub = ShiyiDbHelper.addPersonAddDetail(getRealm(), ShiyiModelHelper.UNNAME_GROUP_NAME, person, personDetail)
                .subscribe(Actions.empty(), throwable -> {
                    getView().dismissLoadingDialog();
                    if (throwable instanceof PersonRepeatThrowable) {
                        getView().showTextDialog("已经存在该联系人, 如果添加将会覆盖重复的属性", "添加", dialog -> {
                            dialog.dismiss();
                            ShiyiDbHelper.coverPersonAddDetail(getRealm(), person, personDetail)
                                    .subscribe(Actions.empty(), throwable2 -> {
                                        getView().dismissLoadingDialog();
                                        DefaultThrowableAction.showThrowableMsg(throwable2);
                                    }, () -> {
                                        getView().dismissLoadingDialog();
                                        getView().removeCurrentItem();
                                    });

                        }, null, null);
                    } else {
                        DefaultThrowableAction.showThrowableMsg(throwable);
                    }
                }, () -> {
                    getView().dismissLoadingDialog();
                    getView().removeCurrentItem();
                });
        addSubscription(addPersonAddDetailSub);
    }

    public Person getPerson(Contact contact) {
        return new Person(contact.getDisplayName(), "", "", "", "");
    }

    public PersonDetail getPersonDetail(Contact contact, String personId) {
        PersonDetail personDetail = new PersonDetail(personId);
        RealmList<Type> types = personDetail.getTypes();

        List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers.size() > 0) {
            Type type = new Type(personId, "电话", types.size() + 1);
            types.add(type);
            for (PhoneNumber phoneNumber : phoneNumbers) {
                type.getTypeDetails().add(new TypeDetail(type.getId(), phoneNumbers.size() + 1, phoneNumber.getNormalizedNumber(), null));
            }
        }
        List<Address> addresses = contact.getAddresses();
        if (addresses.size() > 0) {
            Type type = new Type(personId, "地址", types.size() + 1);
            types.add(type);
            for (Address address : addresses) {
                type.getTypeDetails().add(new TypeDetail(type.getId(), addresses.size() + 1, address.getFormattedAddress(), null));
            }
        }
        Event birthday = contact.getBirthday();
        if (birthday != null) {
            types.add(new Type(personId, "生日", types.size() + 1));
        }
        String note = contact.getNote();
        if (!LshStringUtils.isEmpty(note)) {
            types.add(new Type(personId, "备注", types.size() + 1));
        }
        return personDetail;
    }
}
