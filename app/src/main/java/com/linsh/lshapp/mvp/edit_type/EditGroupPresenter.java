package com.linsh.lshapp.mvp.edit_type;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.action.EmptyConsumer;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;

/**
 * Created by Senh Linsh on 17/5/10.
 * 管理分组
 */
public class EditGroupPresenter extends RealmPresenterImpl<TypeEditContract.View> implements TypeEditContract.Presenter<Group> {

    private RealmResults<Group> mGroups;

    @Override
    protected void attachView() {
        mGroups = ShiyiDbHelper.getGroups(getRealm());
        mGroups.addChangeListener(element -> {
            List<Group> groups = getRealm().copyFromRealm(mGroups);
            getView().setData(groups);
        });
    }

    @Override
    public void detachView() {
        super.detachView();
        mGroups.removeAllChangeListeners();
    }

    @Override
    public void saveTypes(List<Group> groups) {
        Disposable disposable = ShiyiDbHelper.saveGroups(getRealm(), groups)
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> getView().finishActivity());
        addDisposable(disposable);
    }

    @Override
    public void removeType(String typeName, int position) {
        getView().deletedTypeFromRealm(false, position);
    }
}
