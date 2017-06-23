package com.linsh.lshapp.mvp.edit_type;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.Type;
import com.linsh.lshapp.model.event.PersonDetailChangedEvent;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/5/8.
 */

public class EditPersonTypePresenter extends RealmPresenterImpl<TypeEditContract.View> implements TypeEditContract.Presenter<Type> {

    private PersonDetail mPersonDetail;
    public List<Type> mTypes;

    @Override
    protected void attachView() {
        mPersonDetail = ShiyiDbHelper.getPersonDetail(getRealm(), getView().getPersonId());
        mPersonDetail.addChangeListener(element -> {
            if (mPersonDetail.isValid()) {
                mTypes = getRealm().copyFromRealm(mPersonDetail.getTypes());
                getView().setData(mTypes);
            }
        });
    }

    @Override
    public void detachView() {
        super.detachView();
        mPersonDetail.removeAllChangeListeners();
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

    @Override
    public void removeType(String typeName, final int position) {
        ShiyiDbHelper.removePersonType(getRealm(), getView().getPersonId(), typeName)
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
