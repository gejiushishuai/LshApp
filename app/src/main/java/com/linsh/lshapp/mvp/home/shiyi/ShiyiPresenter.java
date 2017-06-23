package com.linsh.lshapp.mvp.home.shiyi;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.NothingAction;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.throwabes.DeleteUnemptyGroupThrowable;
import com.linsh.lshapp.model.throwabes.DeleteUnnameGroupThrowable;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshutils.view.LshColorDialog;

import java.util.List;

import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public class ShiyiPresenter extends RealmPresenterImpl<ShiyiContract.View> implements ShiyiContract.Presenter {

    private RealmResults<Group> mGroups;

    @Override
    protected void attachView() {
        mGroups = ShiyiDbHelper.getGroups(getRealm());
        mGroups.addChangeListener(element -> {
            getView().setData(mGroups);
        });
    }

    @Override
    public void detachView() {
        super.detachView();
        mGroups.removeAllChangeListeners();
    }

    @Override
    public void subscribe() {
        super.subscribe();
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }

    @Override
    public List<Group> getGroups() {
        return mGroups;
    }

    @Override
    public void addGroup(String groupName) {
        Subscription subscription = ShiyiDbHelper.addGroup(getRealm(), groupName)
                .subscribe(new NothingAction<Void>(), new DefaultThrowableAction(),
                        new Action0() {
                            @Override
                            public void call() {
                                getView().setData(mGroups);
                            }
                        });
        addSubscription(subscription);
    }

    @Override
    public void deleteGroup(int position) {
        final Group group = mGroups.get(position);

        Subscription subscription = ShiyiDbHelper.deleteGroup(getRealm(), group.getId())
                .subscribe(new NothingAction<Void>(), new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof DeleteUnnameGroupThrowable) {
                            // 删除未分组分组
                            deleteUnnameGroup();
                        } else if (throwable instanceof DeleteUnemptyGroupThrowable) {
                            // 删除非空分组
                            deleteUnemptyGroup(group.getId());
                        } else {
                            throwable.printStackTrace();
                            getView().showToast(throwable.getMessage());
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        getView().setData(mGroups);
                    }
                });
        addSubscription(subscription);
    }

    private void deleteUnnameGroup() {
        getView().showTextDialog("未分组里的联系人必须移至其他分组后才能删除");
    }

    private void deleteUnemptyGroup(final String groupId) {
        getView().showTextDialog("分组中的联系人不会被删除，是否继续删除该分组？",
                "删除", new LshColorDialog.OnPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog) {
                        dialog.dismiss();
                        ShiyiDbHelper.moveToUnnameGroup(getRealm(), groupId)
                                .subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
                                    @Override
                                    public void call() {
                                        getView().setData(mGroups);
                                    }
                                });
                    }
                }, null, null);
    }

    @Override
    public void renameGroup(int position, String groupName) {
        Group group = mGroups.get(position);

        if (!group.getName().equals(groupName)) {
            Subscription subscription = ShiyiDbHelper.renameGroup(getRealm(), group.getId(), groupName)
                    .subscribe(new NothingAction<Void>(), new DefaultThrowableAction(),
                            new Action0() {
                                @Override
                                public void call() {
                                    getView().setData(mGroups);
                                }
                            });
            addSubscription(subscription);
        }
    }
}
