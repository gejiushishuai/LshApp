package com.linsh.lshapp.mvp.home.huhu;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.bean.db.huhu.Task;
import com.linsh.lshapp.task.db.HuhuDbHelper;

import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public class HuhuPresenter extends RealmPresenterImpl<HuhuContract.View> implements HuhuContract.Presenter {

    private RealmResults<Task> mTasks;

    @Override
    protected void attachView() {
        mTasks = HuhuDbHelper.getTasks(getRealm());
        mTasks.addChangeListener(element -> {
            if (mTasks.isValid()) {
                getView().setData(mTasks);
            }
        });
    }

    @Override
    public void detachView() {
        super.detachView();
        removeRealmChangeListeners(mTasks);
    }


    @Override
    public void deleteTask(long id) {
        Disposable disposable = HuhuDbHelper.deleteTask(getRealm(), id)
                .subscribe();
        addDisposable(disposable);
    }

    @Override
    public void updateAllTasks() {
        Disposable disposable = HuhuDbHelper.updateAllTasks(getRealm())
                .subscribe();
        addDisposable(disposable);
    }

    @Override
    public void finishTask(long id) {
        Disposable disposable = HuhuDbHelper.finishTask(getRealm(), id)
                .subscribe();
        addDisposable(disposable);
    }
}
