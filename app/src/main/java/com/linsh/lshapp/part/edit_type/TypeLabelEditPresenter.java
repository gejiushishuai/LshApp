package com.linsh.lshapp.part.edit_type;

import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.AsyncTransaction;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.TypeLabel;
import com.linsh.lshapp.task.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.task.shiyi.ShiyiDbUtils;
import com.linsh.lshapp.tools.LshRxUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/5/8.
 */

public class TypeLabelEditPresenter extends BasePresenterImpl<TypeEditContract.View> implements TypeEditContract.Presenter<TypeLabel> {

    public List<TypeLabel> mTypeLabels;

    @Override
    protected void attachView() {
        Subscription subscription = ShiyiDbHelper.getTypeLabels(getRealm())
                .subscribe(new Action1<RealmResults<TypeLabel>>() {
                    @Override
                    public void call(RealmResults<TypeLabel> typeLabels) {
                        if (mTypeLabels == null) {
                            mTypeLabels = new RealmList<>();
                        }
                        if (typeLabels != null) {
                            mTypeLabels = getRealm().copyFromRealm(typeLabels);
                            getView().setData(mTypeLabels);
                        }
                    }
                }, new DefaultThrowableAction());
        addSubscription(subscription);
    }


    @Override
    public void saveTypes(final List<TypeLabel> data) {
        LshRxUtils.getAsyncTransactionObservable(getRealm(), new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                ShiyiDbUtils.renewSort(mTypeLabels);
                realm.copyToRealmOrUpdate(data);
            }
        }).subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
            @Override
            public void call() {
                getView().finishActivity();
            }
        });
    }
}
