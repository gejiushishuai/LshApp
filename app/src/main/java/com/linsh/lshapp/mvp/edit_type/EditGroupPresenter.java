package com.linsh.lshapp.mvp.edit_type;

import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import java.util.List;

import io.realm.RealmResults;
import rx.functions.Action0;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/5/10.
 * 管理分组
 */
public class EditGroupPresenter extends BasePresenterImpl<TypeEditContract.View> implements TypeEditContract.Presenter<Group> {

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
        ShiyiDbHelper.saveGroups(getRealm(), groups)
                .subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        getView().finishActivity();
                    }
                });
    }

    @Override
    public void removeType(String typeName, int position) {
        getView().deletedTypeFromRealm(false, position);
    }
}
