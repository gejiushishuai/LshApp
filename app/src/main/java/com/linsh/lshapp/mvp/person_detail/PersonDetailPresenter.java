package com.linsh.lshapp.mvp.person_detail;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.DismissLoadingThrowableAction;
import com.linsh.lshapp.model.action.NothingAction;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.TypeLabel;
import com.linsh.lshapp.model.event.GroupsChangedEvent;
import com.linsh.lshapp.model.event.PersonChangedEvent;
import com.linsh.lshapp.model.event.PersonDetailChangedEvent;
import com.linsh.lshapp.model.result.Result;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import java.util.List;

import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Senh Linsh on 17/4/28.
 */
public class PersonDetailPresenter extends BasePresenterImpl<PersonDetailContract.View> implements PersonDetailContract.Presenter {

    private Person mPerson;
    private PersonDetail mPersonDetail;
    private List<TypeLabel> mTypeLabels;

    @Override
    protected void attachView() {
        Subscription getPersonSub = ShiyiDbHelper.getPerson(getRealm(), getView().getPersonId())
                .subscribe(new Action1<Person>() {
                    @Override
                    public void call(Person person) {
                        if (person != null) {
                            mPerson = person;
                            getView().setData(mPerson);
                        }
                    }
                }, new DefaultThrowableAction());

        Subscription getPersonDetailSub = ShiyiDbHelper.getPersonDetail(getRealm(), getView().getPersonId())
                .subscribe(new Action1<PersonDetail>() {
                    @Override
                    public void call(PersonDetail personDetail) {
                        if (personDetail != null) {
                            mPersonDetail = personDetail;
                            getView().setData(mPersonDetail);
                        }
                    }
                }, new DefaultThrowableAction());
        Subscription getTypeLabelsSub = ShiyiDbHelper.getTypeLabels(getRealm())
                .subscribe(new Action1<RealmResults<TypeLabel>>() {
                    @Override
                    public void call(RealmResults<TypeLabel> typeLabels) {
                        if (typeLabels != null) {
                            mTypeLabels = typeLabels;
                        }
                    }
                }, new DefaultThrowableAction());
        addSubscription(getPersonSub, getPersonDetailSub, getTypeLabelsSub);

        Subscription personChangeBus = RxBus.getDefault().toObservable(PersonChangedEvent.class)
                .subscribe(new Action1<PersonChangedEvent>() {
                    @Override
                    public void call(PersonChangedEvent personChangedEvent) {
                        if (mPerson.isValid()) {
                            getView().setData(mPerson);
                        }
                    }
                });
        Subscription personDetailChangeBus = RxBus.getDefault().toObservable(PersonDetailChangedEvent.class)
                .subscribe(new Action1<PersonDetailChangedEvent>() {
                    @Override
                    public void call(PersonDetailChangedEvent personDetailChangedEvent) {
                        if (mPersonDetail.isValid()) {
                            getView().setData(mPersonDetail);
                        }
                    }
                });
        addRxBusSub(personChangeBus, personDetailChangeBus);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public List<TypeLabel> getTypeLabels() {
        return mTypeLabels;
    }

    @Override
    public void addTypeLabel(final String labelName) {
        Subscription subscription = ShiyiDbHelper.addTypeLabel(getRealm(), labelName, mTypeLabels.size())
                .subscribe(new NothingAction<Void>(), new DefaultThrowableAction(), new Action0() {
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
                .subscribe(new NothingAction<Void>(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        getView().setData(mPersonDetail);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void addType(String typeName, int sort) {
        Subscription subscription = ShiyiDbHelper.addType(getRealm(), mPersonDetail.getId(), typeName, sort)
                .subscribe(new Action1<Result>() {
                    @Override
                    public void call(Result result) {
                        if (!result.isEmpty()) {
                            getView().showToast(result.getMessage());
                        }
                    }
                }, new DismissLoadingThrowableAction(getView()), new Action0() {
                    @Override
                    public void call() {
                        getView().setData(mPersonDetail);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void deleteType(String typeId) {
        Subscription subscription = ShiyiDbHelper.deleteType(getRealm(), typeId)
                .subscribe(new NothingAction<Void>(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        getView().setData(mPersonDetail);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void deleteTypeDetail(String typeDetailId) {
        Subscription subscription = ShiyiDbHelper.deleteTypeDetail(getRealm(), typeDetailId)
                .subscribe(new NothingAction<Void>(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        getView().setData(mPersonDetail);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void deletePerson() {
        Subscription subscription = ShiyiDbHelper.deletePerson(getRealm(), mPersonDetail.getId())
                .subscribe(new NothingAction<Result>(), new DefaultThrowableAction(), new Action0() {
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
