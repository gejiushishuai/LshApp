package com.linsh.lshapp.part.detail;

import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.DismissLoadingAction;
import com.linsh.lshapp.model.action.DismissLoadingThrowableAction;
import com.linsh.lshapp.model.action.NothingAction;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.TypeLabel;
import com.linsh.lshapp.model.result.Result;
import com.linsh.lshapp.tools.ShiyiDataOperator;

import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Senh Linsh on 17/4/28.
 */
public class PersonDetailPresenter extends BasePresenterImpl<PersonDetailContract.View> implements PersonDetailContract.Presenter {

    private Person mPerson;
    private PersonDetail mPersonDetail;
    private RealmList<TypeLabel> mTypeLabels;
    private RealmChangeListener<PersonDetail> mListener = new RealmChangeListener<PersonDetail>() {
        @Override
        public void onChange(PersonDetail element) {
            if (element.isValid()) {
                getView().setData(element);
            }
        }
    };

    @Override
    protected void attachView() {
        Subscription getPersonSub = ShiyiDataOperator.getPerson(getRealm(), getView().getPersonId())
                .subscribe(new Action1<Person>() {
                    @Override
                    public void call(Person person) {
                        if (person != null) {
                            mPerson = person;
                            getView().setData(mPerson);
                        }
                    }
                }, new DefaultThrowableAction());

        Subscription getPersonDetailSub = ShiyiDataOperator.getPersonDetail(getRealm(), getView().getPersonId())
                .subscribe(new Action1<PersonDetail>() {
                    @Override
                    public void call(PersonDetail personDetail) {
                        if (personDetail != null) {
                            mPersonDetail = personDetail;
                            getView().setData(mPersonDetail);
                            mPersonDetail.addChangeListener(mListener);
                        }
                    }
                }, new DefaultThrowableAction());
        Subscription getTypeLabelsSub = ShiyiDataOperator.getTypeLabels(getRealm())
                .subscribe(new Action1<RealmResults<TypeLabel>>() {
                    @Override
                    public void call(RealmResults<TypeLabel> typeLabels) {
                        if (mTypeLabels == null) {
                            mTypeLabels = new RealmList<>();
                        }
                        if (typeLabels != null && typeLabels.size() > 0) {
                            mTypeLabels.addAll(typeLabels.sort("sort"));
                        }
                    }
                }, new DefaultThrowableAction());
        addSubscription(getPersonSub, getPersonDetailSub, getTypeLabelsSub);
    }

    @Override
    public void detachView() {
        super.detachView();
        mPersonDetail.removeAllChangeListeners();
    }

    @Override
    public RealmList<TypeLabel> getTypes() {
        return mTypeLabels;
    }

    @Override
    public void addTypeLabel(final String labelName) {
        Subscription subscription = ShiyiDataOperator.addTypeLabel(getRealm(), labelName, mTypeLabels.size())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        addType(labelName);
                    }
                }, new DefaultThrowableAction());
        addSubscription(subscription);
    }

    @Override
    public void addType(String typeName) {
        getView().showLoadingDialog();

        Subscription subscription = ShiyiDataOperator.addType(getRealm(), mPersonDetail.getId(), typeName)
                .subscribe(new DismissLoadingAction<Void>(getView()), new DismissLoadingThrowableAction(getView()));
        addSubscription(subscription);
    }

    @Override
    public void addType(String typeName, int sort) {
        getView().showLoadingDialog();

        Subscription subscription = ShiyiDataOperator.addType(getRealm(), mPersonDetail.getId(), typeName, sort)
                .subscribe(new Action1<Result>() {
                    @Override
                    public void call(Result result) {
                        getView().dismissLoadingDialog();
                        if (result != null && !result.isEmpty()) {
                            getView().showToast(result.getMessage());
                        }
                    }
                }, new DismissLoadingThrowableAction(getView()));
        addSubscription(subscription);
    }

    @Override
    public void deleteType(String typeId) {
        Subscription subscription = ShiyiDataOperator.deleteType(getRealm(), typeId)
                .subscribe(new NothingAction<Void>(), new DefaultThrowableAction());
        addSubscription(subscription);
    }

    @Override
    public void deleteTypeDetail(String typeDetailId) {
        Subscription subscription = ShiyiDataOperator.deleteTypeDetail(getRealm(), typeDetailId)
                .subscribe(new NothingAction<Void>(), new DefaultThrowableAction());
        addSubscription(subscription);
    }

    @Override
    public void deletePerson() {
        getView().showLoadingDialog();

        Subscription subscription = ShiyiDataOperator.deletePerson(getRealm(), mPersonDetail.getId())
                .subscribe(new Action1<Result>() {
                    @Override
                    public void call(Result result) {
                        getView().dismissLoadingDialog();
                        getView().finishActivity();
                    }
                }, new DismissLoadingThrowableAction(getView()));
        addSubscription(subscription);
    }
}
