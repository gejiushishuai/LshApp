package com.linsh.lshapp.mvp.edit_task;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.db.huhu.Task;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface TaskEditContract {

    interface View extends BaseContract.BaseView {

        long getHuhuTaskId();

        void setData(Task task);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void checkName(String taskName);

        void saveTask(long taskId, String name, String frequency);
    }
}
