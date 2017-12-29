package com.linsh.lshapp.task.db;

import com.linsh.lshapp.model.action.AsyncConsumer;
import com.linsh.lshapp.model.action.AsyncTransaction;
import com.linsh.lshapp.model.bean.db.huhu.Task;
import com.linsh.lshapp.model.result.Result;
import com.linsh.lshapp.tools.LshRxUtils;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/15
 *    desc   :
 * </pre>
 */
public class HuhuDbHelper {


    public static RealmResults<Task> getTasks(Realm realm) {
        return realm.where(Task.class).findAllSortedAsync("id");
    }

    public static Task getTask(Realm realm, long taskId) {
        return realm.where(Task.class).equalTo("id", taskId).findFirstAsync();
    }

    public static Flowable<Boolean> hasTaskName(String name) {
        return LshRxUtils.getAsyncFlowable(new AsyncConsumer<Boolean>() {
            @Override
            public void call(Realm realm, FlowableEmitter<? super Boolean> emitter) {
                Task title = realm.where(Task.class).equalTo("title", name).findFirst();
                emitter.onNext(title != null);
            }
        });
    }

    public static Flowable<Result> updateOrCreateTask(Realm realm, long taskId, String title, int frequency, char unit) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                long newTaskId = taskId;
                if (newTaskId <= 0) {
                    Number maxId = realm.where(Task.class).max("id");
                    newTaskId = maxId == null ? 1 : maxId.longValue() + 1;
                }
                Task task = new Task(newTaskId, title, frequency, unit);
                realm.copyToRealmOrUpdate(task);
                emitter.onNext(new Result());
            }
        });
    }


    public static Flowable<Result> updateAllTasks(Realm realm) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                RealmResults<Task> tasks = realm.where(Task.class).findAll();
                for (Task task : tasks) {
                    task.update();
                }
                emitter.onNext(new Result());
            }
        });
    }

    public static Flowable<Result> deleteTask(Realm realm, long taskId) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                RealmResults<Task> tasks = realm.where(Task.class).equalTo("id", taskId).findAll();
                if (tasks.size() > 0) {
                    tasks.deleteAllFromRealm();
                }
            }
        });
    }

    public static Flowable<Result> finishTask(Realm realm, long id) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Result> emitter) {
                Task task = realm.where(Task.class).equalTo("id", id).findFirst();
                if (task != null) {
                    task.finishTaskOneTime();
                }
            }
        });
    }
}
