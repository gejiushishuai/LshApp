package com.linsh.lshapp.mvp.home.yingmao;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.SignIn;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public interface YingmaoContract {


    interface View extends BaseContract.BaseView {

        void setData(List<SignIn> values);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void signIn(SignIn signIn);

        void switchIgnore(SignIn signIn);

        void signInAll();
    }
}
