package com.linsh.lshapp.mvp.home.shiyi;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.action.EmptyConsumer;
import com.linsh.lshapp.model.action.NothingConsumer;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.throwabes.DeleteUnemptyGroupThrowable;
import com.linsh.lshapp.model.throwabes.DeleteUnnameGroupThrowable;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshutils.view.LshColorDialog;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public class ShiyiPresenter extends RealmPresenterImpl<ShiyiContract.View> implements ShiyiContract.Presenter {

    private RealmResults<Group> mGroups;

    @Override
    protected void attachView() {
        mGroups = ShiyiDbHelper.getGroups(getRealm());
        mGroups.addChangeListener(element -> {
            getView().setData(getRealm().copyFromRealm(mGroups));
        });
    }

    @Override
    public void detachView() {
        mGroups.removeAllChangeListeners();
        super.detachView();
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
    public void syncPersonsInGroup(int position) {
        Group group = mGroups.get(position);
        Disposable disposable = ShiyiDbHelper.syncPersonsInGroup(getRealm(), group.getId())
                .subscribe(new EmptyConsumer<Void>(), new DefaultThrowableConsumer());
        addDisposable(disposable);
    }

    @Override
    public void addGroup(String groupName) {
        Disposable disposable = ShiyiDbHelper.addGroup(getRealm(), groupName)
                .subscribe(new NothingConsumer<>(), new DefaultThrowableConsumer());
        addDisposable(disposable);
    }

    @Override
    public void deleteGroup(int position) {
        final Group group = mGroups.get(position);

        Disposable disposable = ShiyiDbHelper.deleteGroup(getRealm(), group.getId())
                .subscribe(new NothingConsumer<Void>(), throwable -> {
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
                });
        addDisposable(disposable);
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
                        Disposable disposable = ShiyiDbHelper.moveToUnnameGroup(getRealm(), groupId)
                                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer());
                        addDisposable(disposable);
                    }
                }, null, null);
    }

    @Override
    public void renameGroup(int position, String groupName) {
        Group group = mGroups.get(position);

        if (!group.getName().equals(groupName)) {
            Disposable disposable = ShiyiDbHelper.renameGroup(getRealm(), group.getId(), groupName)
                    .subscribe(new NothingConsumer<Void>(), new DefaultThrowableConsumer());
            addDisposable(disposable);
        }
    }
}
