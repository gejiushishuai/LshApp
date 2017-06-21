package com.linsh.lshapp.mvp.person_detail;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.DismissLoadingThrowableAction;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.TypeLabel;
import com.linsh.lshapp.model.event.GroupsChangedEvent;
import com.linsh.lshapp.model.event.PersonChangedEvent;
import com.linsh.lshapp.model.event.PersonDetailChangedEvent;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import java.util.List;

import io.realm.RealmObject;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/4/28.
 */
public class PersonDetailPresenter extends BasePresenterImpl<PersonDetailContract.View> implements PersonDetailContract.Presenter {

    private Person mPerson;
    private PersonDetail mPersonDetail;
    private List<TypeLabel> mTypeLabels;

    @Override
    protected void attachView() {
        // 获取联系人信息
        Subscription getPersonSub = ShiyiDbHelper.getPerson(getRealm(), getView().getPersonId())
                .subscribe(person -> {
                    if (person != null) {
                        mPerson = person;
                        getView().setData(mPerson);
                        RealmObject.removeChangeListener(mPerson, element -> {
                            getView().setData(mPerson);
                        });
                    }
                }, new DefaultThrowableAction());
        // 获取联系人详情
        Subscription getPersonDetailSub = ShiyiDbHelper.getPersonDetail(getRealm(), getView().getPersonId())
                .subscribe(personDetail -> {
                    if (personDetail != null) {
                        mPersonDetail = personDetail;
                        getView().setData(mPersonDetail);
                        RealmObject.removeChangeListener(mPersonDetail, element -> {
                            getView().setData(mPersonDetail);
                        });
                    }
                }, new DefaultThrowableAction());
        // 获取类型标签
        Subscription getTypeLabelsSub = ShiyiDbHelper.getTypeLabels(getRealm())
                .subscribe(typeLabels -> {
                    if (typeLabels != null) {
                        mTypeLabels = typeLabels;
                    }
                }, new DefaultThrowableAction());
        addSubscription(getPersonSub, getPersonDetailSub, getTypeLabelsSub);



        // 添加联系人和联系人详情变动的广播
        Subscription personChangeBus = RxBus.getDefault().toObservable(PersonChangedEvent.class)
                .subscribe(personChangedEvent -> {
                    if (mPerson.isValid()) {
                        getView().setData(mPerson);
                    }
                });
        Subscription personDetailChangeBus = RxBus.getDefault().toObservable(PersonDetailChangedEvent.class)
                .subscribe(personDetailChangedEvent -> {
                    if (mPersonDetail.isValid()) {
                        getView().setData(mPersonDetail);
                    }
                });
        addRxBusSub(personChangeBus, personDetailChangeBus);
    }

    @Override
    public void detachView() {
        super.detachView();
        RealmObject.removeAllChangeListeners(mPerson);
        RealmObject.removeAllChangeListeners(mPersonDetail);
    }

    @Override
    public List<TypeLabel> getTypeLabels() {
        return mTypeLabels;
    }

    @Override
    public void addTypeLabel(final String labelName) {
        Subscription subscription = ShiyiDbHelper.addTypeLabel(getRealm(), labelName, mTypeLabels.size())
                .subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        addType(labelName);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void addType(String typeName) {
        Subscription subscription = ShiyiDbHelper.addType(getRealm(), mPersonDetail.getId(), typeName)
                .subscribe(Actions.empty(), new DefaultThrowableAction(), Actions.empty());
        addSubscription(subscription);
    }

    @Override
    public void addType(String typeName, int sort) {
        Subscription subscription = ShiyiDbHelper.addType(getRealm(), mPersonDetail.getId(), typeName, sort)
                .subscribe(Actions.empty(), new DismissLoadingThrowableAction(getView()), Actions.empty());
        addSubscription(subscription);
    }

    @Override
    public void deleteType(String typeId) {
        Subscription subscription = ShiyiDbHelper.deleteType(getRealm(), typeId)
                .subscribe(Actions.empty(), new DefaultThrowableAction(), Actions.empty());
        addSubscription(subscription);
    }

    @Override
    public void deleteTypeDetail(String typeDetailId) {
        Subscription subscription = ShiyiDbHelper.deleteTypeDetail(getRealm(), typeDetailId)
                .subscribe(Actions.empty(), new DefaultThrowableAction(), Actions.empty());
        addSubscription(subscription);
    }

    @Override
    public void deletePerson() {
        Subscription subscription = ShiyiDbHelper.deletePerson(getRealm(), mPersonDetail.getId())
                .subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        RxBus.getDefault().post(new GroupsChangedEvent());
                        getView().finishActivity();
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public PersonDetail getPersonDetail() {
        return mPersonDetail;
    }

    @Override
    public Person getPerson() {
        return mPerson;
    }
}
