package com.linsh.lshapp.mvp.edit_type;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.action.EmptyConsumer;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.Type;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import java.util.List;

import io.reactivex.disposables.Disposable;

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
        Disposable disposable = ShiyiDbHelper.savePersonTypes(getRealm(), getView().getPersonId(), mTypes)
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> {
                    getView().finishActivity();
                });
        addDisposable(disposable);
    }

    @Override
    public void removeType(String typeName, final int position) {
        Disposable disposable = ShiyiDbHelper.removePersonType(getRealm(), getView().getPersonId(), typeName)
                .subscribe(new EmptyConsumer<>(), throwable -> {
                    DefaultThrowableConsumer.showThrowableMsg(throwable);
                    getView().deletedTypeFromRealm(false, position);
                }, () -> getView().deletedTypeFromRealm(true, position));
        addDisposable(disposable);
    }
}
