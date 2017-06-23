package com.linsh.lshapp.mvp.type_detail;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.db.TypeDetail;
import com.linsh.lshapp.model.event.PersonDetailChangedEvent;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class TypeDetailPresenter extends BasePresenterImpl<TypeDetailContract.View> implements TypeDetailContract.Presenter {

    private TypeDetail mTypeDetail;

    @Override
    protected void attachView() {

        mTypeDetail = ShiyiDbHelper.getTypeDetail(getRealm(), getView().getTypeDetailId());
        mTypeDetail.addChangeListener(element -> {
            if (mTypeDetail.isValid()) {
                getView().setData(mTypeDetail);
            }
        });
    }

    @Override
    public void detachView() {
        super.detachView();
        mTypeDetail.removeAllChangeListeners();
    }

    @Override
    public void saveTypeDetail(String info, String desc) {
        if (!info.equals(mTypeDetail.getDetail()) || !desc.equals(mTypeDetail.getDescribe())) {
            Subscription subscription = ShiyiDbHelper.editTypeDetail(getRealm(), mTypeDetail.getId(), info, desc)
                    .subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
                        @Override
                        public void call() {
                            RxBus.getDefault().post(new PersonDetailChangedEvent());
                            getView().finishActivity();
                        }
                    });
            addSubscription(subscription);
        } else {
            getView().finishActivity();
        }
    }

    @Override
    public void deleteTypeDetail() {
        Subscription subscription = ShiyiDbHelper.deleteTypeDetail(getRealm(), mTypeDetail.getId())
                .subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        RxBus.getDefault().post(new PersonDetailChangedEvent());
                        getView().finishActivity();
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public TypeDetail getTypeDetail() {
        return mTypeDetail;
    }
}
