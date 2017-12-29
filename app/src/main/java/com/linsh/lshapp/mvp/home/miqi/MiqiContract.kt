package com.linsh.lshapp.mvp.home.miqi

import com.linsh.lshapp.base.BaseContract
import com.linsh.lshapp.model.bean.db.miqi.Account

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/22
 *    desc   :
 * </pre>
 */
interface MiqiContract {

    interface View : BaseContract.BaseView {
        fun setData(accounts: List<Account>)


    }

    interface Presenter : BaseContract.BasePresenter<View> {

    }

}