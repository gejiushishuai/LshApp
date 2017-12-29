package com.linsh.lshapp.mvp.person_detail;

import com.linsh.utilseverywhere.LogUtils;
import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.action.DismissLoadingThrowableConsumer;
import com.linsh.lshapp.model.action.EmptyConsumer;
import com.linsh.lshapp.model.bean.db.shiyi.Person;
import com.linsh.lshapp.model.bean.db.shiyi.PersonAlbum;
import com.linsh.lshapp.model.bean.db.shiyi.PersonDetail;
import com.linsh.lshapp.model.bean.db.shiyi.TypeLabel;
import com.linsh.lshapp.task.db.ShiyiDbHelper;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;

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

        PersonAlbum personAlbum = getRealm().where(PersonAlbum.class).equalTo("id", getView().getPersonId()).findFirst();
        if (personAlbum != null) {
            LogUtils.i("personAlbum size = " + personAlbum.getAvatars().size());
        } else {
            LogUtils.i("personAlbum = null");
        }
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
        Disposable disposable = ShiyiDbHelper.addTypeLabel(getRealm(), labelName, mTypeLabels.size())
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> addType(labelName));
        addDisposable(disposable);
    }

    @Override
    public void addType(String typeName) {
        Disposable disposable = ShiyiDbHelper.addType(getRealm(), mPersonDetail.getId(), typeName)
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer());
        addDisposable(disposable);
    }

    @Override
    public void addType(String typeName, int sort) {
        Disposable disposable = ShiyiDbHelper.addType(getRealm(), mPersonDetail.getId(), typeName, sort)
                .subscribe(new EmptyConsumer<>(), new DismissLoadingThrowableConsumer(getView()));
        addDisposable(disposable);
    }

    @Override
    public void deleteType(String typeId) {
        Disposable disposable = ShiyiDbHelper.deleteType(getRealm(), typeId)
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer());
        addDisposable(disposable);
    }

    @Override
    public void deleteTypeDetail(String typeDetailId) {
        Disposable disposable = ShiyiDbHelper.deleteTypeDetail(getRealm(), typeDetailId)
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer());
        addDisposable(disposable);
    }

    @Override
    public void deletePerson() {
        Disposable disposable = ShiyiDbHelper.deletePerson(getRealm(), mPerson.getId())
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> getView().finishActivity());
        addDisposable(disposable);
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
