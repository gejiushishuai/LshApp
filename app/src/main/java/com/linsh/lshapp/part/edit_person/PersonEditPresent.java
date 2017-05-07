package com.linsh.lshapp.part.edit_person;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.NothingAction;
import com.linsh.lshapp.model.bean.Group;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.event.GroupsChangedEvent;
import com.linsh.lshapp.model.event.PersonChangedEvent;
import com.linsh.lshapp.task.shiyi.ShiyiDbHelper;

import java.util.List;

import io.realm.RealmList;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class PersonEditPresent extends BasePresenterImpl<PersonEditContract.View> implements PersonEditContract.Presenter {

    private RealmList<Group> mGroups;
    private Person mPerson;

    @Override
    protected void attachView() {
        Subscription getGroupsSub = ShiyiDbHelper.getGroups(getRealm())
                .subscribe(new Action1<RealmList<Group>>() {
                    @Override
                    public void call(RealmList<Group> groups) {
                        mGroups = groups;
                    }
                }, new DefaultThrowableAction());
        addSubscription(getGroupsSub);

        String personId = getView().getPersonId();
        if (personId != null) {
            Subscription getPersonSub = ShiyiDbHelper.getPerson(getRealm(), personId)
                    .subscribe(new Action1<Person>() {
                        @Override
                        public void call(Person person) {
                            if (person != null) {
                                mPerson = person;
                                getView().setData(mPerson);
                            }
                        }
                    }, new DefaultThrowableAction());
            addSubscription(getPersonSub);
        }
    }

    @Override
    public List<Group> getGroups() {
        return mGroups;
    }

    @Override
    public void addGroup(final String inputText) {
        ShiyiDbHelper.addGroup(getRealm(), inputText)
                .subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        getView().setGroup(inputText);
                        getView().onPersonModified();
                        RxBus.getDefault().post(new GroupsChangedEvent());
                    }
                });
    }

    @Override
    public void savePerson(String group, String name, String desc, String sex) {
        if (mPerson == null) {
            // 创建Person
            ShiyiDbHelper.addPerson(getRealm(), group, name, desc, sex)
                    .subscribe(new NothingAction<Void>(), new DefaultThrowableAction(), new Action0() {
                        @Override
                        public void call() {
                            RxBus.getDefault().post(new GroupsChangedEvent());
                            RxBus.getDefault().post(new PersonChangedEvent());
                            getView().finishActivity();
                        }
                    });
        } else {
            // 属性有变化, 则修改Person属性
            Observable<Void> observable = null;
            if (!name.equals(mPerson.getName()) || !desc.equals(mPerson.getDescribe())
                    || !sex.equals(mPerson.getGender())) {
                observable = ShiyiDbHelper.editPerson(getRealm(), mPerson.getId(), name, desc, sex);
            }
            // 组别有变化, 则修改组别
            String primaryGroup = getView().getPrimaryGroup();
            if (!group.equals(primaryGroup)) {
                final Observable<Void> movePersonToGroup = ShiyiDbHelper.movePersonToGroup(getRealm(), mPerson.getId(), group);
                if (observable == null) {
                    observable = movePersonToGroup;
                } else {
                    observable.flatMap(new Func1<Void, Observable<?>>() {
                        @Override
                        public Observable<?> call(Void aVoid) {
                            return movePersonToGroup;
                        }
                    });
                }
            }
            if (observable == null) {
                getView().finishActivity();
            } else {
                observable.subscribe(new NothingAction<Void>(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        RxBus.getDefault().post(new GroupsChangedEvent());
                        RxBus.getDefault().post(new PersonChangedEvent());
                        getView().finishActivity();
                    }
                });
            }
        }
    }
}
