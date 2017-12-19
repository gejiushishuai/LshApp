package com.linsh.lshapp.mvp.edit_task;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.bean.db.huhu.Frequency;
import com.linsh.lshapp.model.bean.db.huhu.Task;
import com.linsh.lshapp.task.db.shiyi.HuhuDbHelper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class TaskEditPresent extends RealmPresenterImpl<TaskEditContract.View> implements TaskEditContract.Presenter {

    private Task mTask;

    @Override
    protected void attachView() {
        long taskId = getView().getHuhuTaskId();
        if (taskId > 0) {
            mTask = HuhuDbHelper.getTask(getRealm(), taskId);
            mTask.addChangeListener(new RealmChangeListener<RealmModel>() {
                @Override
                public void onChange(RealmModel element) {
                    if (mTask.isValid()) {
                        getView().setData(mTask);
                    }
                }
            });
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mTask != null && mTask.isValid()) {
            mTask.removeAllChangeListeners();
        }
    }

    @Override
    public void checkName(String name) {
        Disposable disposable = HuhuDbHelper.hasTaskName(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(has -> {
                    if (has) {
                        getView().showToast("该任务已经存在了哦");
                    }
                });
        addDisposable(disposable);
    }

    @Override
    public void saveTask(long taskId, String name, String frequency) {
        Frequency parse = Frequency.parse(frequency);
        if (parse != null && parse.frequency > 0) {
            Disposable disposable = HuhuDbHelper.updateOrCreateTask(getRealm(), taskId, name, parse.frequency, parse.unit)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        getView().finishActivity();
                    });
            addDisposable(disposable);
        } else {
            getView().showToast("频率解析失败, 无法保存");
        }
    }
}
