package com.linsh.lshapp.mvp.edit_type;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.event.GroupsChangedEvent;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import java.util.List;

import io.realm.RealmList;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/5/10.
 */
public class EditGroupPresenter extends BasePresenterImpl<TypeEditContract.View> implements TypeEditContract.Presenter<Group> {

    private List<Group> mGroups;

    @Override
    protected void attachView() {
        Subscription subscription = ShiyiDbHelper.getGroups(getRealm())
                .subscribe(new Action1<RealmList<Group>>() {
                    @Override
                    public void call(RealmList<Group> groups) {
                        if (groups != null) {
                            mGroups = getRealm().copyFromRealm(groups);
                            getView().setData(mGroups);
                        }
                    }
                }, new DefaultThrowableAction());
        addSubscription(subscription);
    }

    @Override
    public void saveTypes(List<Group> groups) {
        ShiyiDbHelper.saveGroups(getRealm(), groups)
                .subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
                    @Override
                    public void call() {
                        RxBus.getDefault().post(new GroupsChangedEvent());
                        getView().finishActivity();
                    }
                });
    }

    @Override
    public void removeType(String typeName, int position) {
        getView().deletedTypeFromRealm(false, position);
    }
}
