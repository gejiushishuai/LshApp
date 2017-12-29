package com.linsh.lshapp.mvp.type_detail;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.action.EmptyConsumer;
import com.linsh.lshapp.model.bean.db.shiyi.TypeDetail;
import com.linsh.lshapp.task.db.ShiyiDbHelper;

import io.reactivex.disposables.Disposable;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class TypeDetailPresenter extends RealmPresenterImpl<TypeDetailContract.View> implements TypeDetailContract.Presenter {

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
            Disposable disposable = ShiyiDbHelper.editTypeDetail(getRealm(), mTypeDetail.getId(), info, desc)
                    .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> {
                        getView().finishActivity();
                    });
            addDisposable(disposable);
        } else {
            getView().finishActivity();
        }
    }

    @Override
    public void deleteTypeDetail() {
        Disposable disposable = ShiyiDbHelper.deleteTypeDetail(getRealm(), mTypeDetail.getId())
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> {
                    getView().finishActivity();
                });
        addDisposable(disposable);
    }

    @Override
    public TypeDetail getTypeDetail() {
        return mTypeDetail;
    }
}
