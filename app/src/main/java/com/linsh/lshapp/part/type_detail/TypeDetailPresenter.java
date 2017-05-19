package com.linsh.lshapp.part.type_detail;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.DismissLoadingAction;
import com.linsh.lshapp.model.bean.TypeDetail;
import com.linsh.lshapp.model.event.PersonDetailChangedEvent;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class TypeDetailPresenter extends BasePresenterImpl<TypeDetailContract.View> implements TypeDetailContract.Presenter {

    private TypeDetail mTypeDetail;

    @Override
    protected void attachView() {
        getView().showLoadingDialog();

        Subscription subscription = ShiyiDbHelper.getTypeDetail(getRealm(), getView().getTypeDetailId())
                .subscribe(new Action1<TypeDetail>() {
                    @Override
                    public void call(TypeDetail typeDetail) {
                        getView().dismissLoadingDialog();
                        if (typeDetail != null) {
                            mTypeDetail = typeDetail;
                            getView().setData(mTypeDetail);
                        }
                    }
                }, new DismissLoadingAction<Throwable>(getView()));
        addSubscription(subscription);
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
