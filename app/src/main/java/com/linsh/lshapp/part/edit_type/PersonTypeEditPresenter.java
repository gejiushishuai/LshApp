package com.linsh.lshapp.part.edit_type;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.Type;
import com.linsh.lshapp.model.event.PersonDetailChangedEvent;
import com.linsh.lshapp.task.shiyi.ShiyiDbHelper;

import java.util.List;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/5/8.
 */

public class PersonTypeEditPresenter extends BasePresenterImpl<TypeEditContract.View> implements TypeEditContract.Presenter<Type> {

    public List<Type> mTypes;

    @Override
    protected void attachView() {
        Subscription subscription = ShiyiDbHelper.getPersonDetail(getRealm(), getView().getPersonId())
                .subscribe(new Action1<PersonDetail>() {
                    @Override
                    public void call(PersonDetail personDetail) {
                        if (personDetail != null) {
                            mTypes = getRealm().copyFromRealm(personDetail.getTypes());
                            getView().setData(mTypes);
                        }
                    }
                }, new DefaultThrowableAction());
        addSubscription(subscription);
    }


    @Override
    public void saveTypes(final List<Type> data) {
        ShiyiDbHelper.savePersonTypes(getRealm(), getView().getPersonId(), mTypes)
                .subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        getView().finishActivity();
                        RxBus.getDefault().post(new PersonDetailChangedEvent());
                    }
                });
    }
}
