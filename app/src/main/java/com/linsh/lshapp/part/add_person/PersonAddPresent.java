package com.linsh.lshapp.part.add_person;

import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.DismissLoadingThrowableAction;
import com.linsh.lshapp.model.bean.Group;
import com.linsh.lshapp.tools.ShiyiDataOperator;

import java.util.List;

import io.realm.RealmList;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class PersonAddPresent extends BasePresenterImpl<PersonAddContract.View> implements PersonAddContract.Presenter {

    private RealmList<Group> mGroups;

    @Override
    protected void attachView() {
        Subscription getGroupsSub = ShiyiDataOperator.getGroups(getRealm())
                .subscribe(new Action1<RealmList<Group>>() {
                    @Override
                    public void call(RealmList<Group> groups) {
                        mGroups = groups;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        getView().showToast(throwable.getMessage());
                    }
                });
        addSubscription(getGroupsSub);
    }

    @Override
    public List<Group> getGroups() {
        return mGroups;
    }

    @Override
    public void addGroup(String inputText) {
        ShiyiDataOperator.addGroup(getRealm(), inputText)
                .subscribe(Actions.empty(), new DefaultThrowableAction());
    }

    @Override
    public void addPerson(String group, String name, String desc, String sex) {
        getView().showLoadingDialog();
        ShiyiDataOperator.addPerson(getRealm(), group, name, desc, sex)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        getView().dismissLoadingDialog();
                        getView().addPersonSuccess();
                    }
                }, new DismissLoadingThrowableAction(getView()));
    }
}
