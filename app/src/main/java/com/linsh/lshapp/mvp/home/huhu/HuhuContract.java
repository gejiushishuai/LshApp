package com.linsh.lshapp.mvp.home.huhu;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.db.huhu.Task;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public interface HuhuContract {


    interface View extends BaseContract.BaseView {

        void setData(List<Task> values);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void deleteTask(long id);

        void updateAllTasks();

        void finishTask(long id);
    }
}
