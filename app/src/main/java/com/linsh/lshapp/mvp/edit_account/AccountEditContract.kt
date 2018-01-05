package com.linsh.lshapp.mvp.edit_account

import com.linsh.lshapp.base.BaseContract
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.model.bean.db.miqi.AccountAvatar
import com.linsh.lshapp.model.bean.db.miqi.Website

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/23
 *    desc   :
 * </pre>
 */
interface AccountEditContract {

    interface View : BaseContract.BaseView {
        fun getAccountId(): Long
        fun setData(mAccount: Account)

    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun getWebsites(): List<Website>?
        fun saveAccount(name: String, website: String, avatar: AccountAvatar?)
        fun addWebsite(website: String)
    }
}
