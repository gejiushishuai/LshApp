package com.linsh.lshapp.part.detail;

import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.DismissLoadingThrowableAction;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.TypeLabel;
import com.linsh.lshapp.tools.ShiyiDataOperator;

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
    public RealmList<TypeLabel> getTypes() {
        return mTypeLabels;
    }

    @Override
    public void addTypeLabel(final String labelName) {
        ShiyiDataOperator.addTypeLabel(getRealm(), labelName, mTypeLabels.size())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        addType(labelName);
                    }
                }, new DefaultThrowableAction());
    }

    @Override
    public void addType(String typeName) {
        getView().showLoadingDialog();
        ShiyiDataOperator.addType(getRealm(), mPersonDetail.getId(), typeName)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        getView().dismissLoadingDialog();
                        getView().setData(mPersonDetail);
                    }
                }, new DismissLoadingThrowableAction(getView()));
    }
}
