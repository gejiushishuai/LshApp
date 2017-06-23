package com.linsh.lshapp.mvp.person_detail;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.DismissLoadingThrowableAction;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.TypeLabel;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import java.util.List;

import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/4/28.
 */
public class PersonDetailPresenter extends RealmPresenterImpl<PersonDetailContract.View> implements PersonDetailContract.Presenter {

    private Person mPerson;
    private PersonDetail mPersonDetail;
    private RealmResults<TypeLabel> mTypeLabels;

    @Override
    protected void attachView() {
        // 获取联系人信息
        mPerson = ShiyiDbHelper.getPerson(getRealm(), getView().getPersonId());
        mPerson.addChangeListener(element -> {
            if (mPerson.isValid()) {
                getView().setData(mPerson);
                mPerson.removeAllChangeListeners();
            }
        });
        // 获取联系人详情
        mPersonDetail = ShiyiDbHelper.getPersonDetail(getRealm(), getView().getPersonId());
        mPersonDetail.addChangeListener(element -> {
            if (mPersonDetail.isValid()) {
                getView().setData(mPersonDetail);
                mPersonDetail.removeAllChangeListeners();
            }
        });
        // 获取类型标签
        mTypeLabels = ShiyiDbHelper.getTypeLabels(getRealm());
    }

    @Override
    public void invalidateView() {
        super.invalidateView();
        if (mPerson.isValid()) {
            getView().setData(mPerson);
        }
        if (mPersonDetail.isValid()) {
            getView().setData(mPersonDetail);
        }
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
                .subscribe(Actions.empty(), new DefaultThrowableAction(), () -> getView().finishActivity());
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
