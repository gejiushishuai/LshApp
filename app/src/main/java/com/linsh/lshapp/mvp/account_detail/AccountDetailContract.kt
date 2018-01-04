package com.linsh.lshapp.mvp.edit_account

import com.linsh.lshapp.base.BaseContract
import com.linsh.lshapp.model.bean.db.miqi.Account

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/23
 *    desc   :
 * </pre>
 */
interface AccountDetailContract {

    interface View : BaseContract.BaseView {
        fun getAccountId(): Long
        fun setData(account: Account)

    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun addLoginWay(accountId: Long)
        fun addLoginWay(accountName: String)
    }
}
