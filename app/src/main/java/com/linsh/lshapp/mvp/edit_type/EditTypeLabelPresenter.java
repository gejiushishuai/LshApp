package com.linsh.lshapp.mvp.edit_type;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.AsyncTransaction;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.db.TypeLabel;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbUtils;
import com.linsh.lshapp.tools.LshRxUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/5/8.
 */

public class EditTypeLabelPresenter extends RealmPresenterImpl<TypeEditContract.View> implements TypeEditContract.Presenter<TypeLabel> {

    private RealmResults<TypeLabel> mTypeLabels;

    @Override
    protected void attachView() {
        mTypeLabels = ShiyiDbHelper.getTypeLabels(getRealm());
        mTypeLabels.addChangeListener(element -> {
            if (mTypeLabels.isValid()) {
                List<TypeLabel> typeLabels = getRealm().copyFromRealm(mTypeLabels);
                getView().setData(typeLabels);
            }
        });
    }

    @Override
    public void detachView() {
        super.detachView();
        mTypeLabels.removeAllChangeListeners();
    }

    @Override
    public void saveTypes(final List<TypeLabel> data) {
        if (mTypeLabels == null) return;

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

    @Override
    public void removeType(String typeName, final int position) {
        ShiyiDbHelper.removeTypeLabel(getRealm(), typeName)
                .subscribe(Actions.empty(), new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        DefaultThrowableAction.showThrowableMsg(throwable);
                        getView().deletedTypeFromRealm(false, position);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        getView().deletedTypeFromRealm(true, position);
                    }
                });
    }
}
