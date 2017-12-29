package com.linsh.lshapp.mvp.edit_type;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.AsyncTransaction;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.action.EmptyConsumer;
import com.linsh.lshapp.model.bean.db.shiyi.TypeLabel;
import com.linsh.lshapp.task.db.ShiyiDbHelper;
import com.linsh.lshapp.task.db.ShiyiDbUtils;
import com.linsh.lshapp.tools.LshRxUtils;

import java.util.List;

import io.reactivex.FlowableEmitter;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;

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

        Disposable disposable = LshRxUtils.getAsyncTransactionFlowable(getRealm(), new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                ShiyiDbUtils.renewSort(data);
                realm.copyToRealmOrUpdate(data);
            }
        }).subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> {
            getView().finishActivity();
        });
        addDisposable(disposable);
    }

    @Override
    public void removeType(String typeName, final int position) {
        ShiyiDbHelper.removeTypeLabel(getRealm(), typeName)
                .subscribe(new EmptyConsumer<>(), throwable -> {
                    DefaultThrowableConsumer.showThrowableMsg(throwable);
                    getView().deletedTypeFromRealm(false, position);
                }, () -> getView().deletedTypeFromRealm(true, position));
    }
}
