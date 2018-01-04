package com.linsh.lshapp.mvp.edit_account

import com.linsh.lshapp.base.RealmPresenterImpl
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.model.bean.db.miqi.Website
import com.linsh.lshapp.model.consumer.ResultConsumer
import com.linsh.lshapp.model.consumer.ThrowableConsumer
import com.linsh.lshapp.task.db.MiqiDbHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.RealmChangeListener
import io.realm.RealmResults

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/23
 *    desc   :
 * </pre>
 */
class AccountDetailPresent : RealmPresenterImpl<AccountDetailContract.View>(), AccountDetailContract.Presenter {

    private lateinit var mWebsites: RealmResults<Website>
    private var mAccount: Account? = null

    override fun attachView() {
        val accountId = view.getAccountId()
        if (accountId > 0) {
            mAccount = MiqiDbHelper.getAccount(realm, accountId)
            mAccount!!.addChangeListener(RealmChangeListener {
                if (mAccount!!.isValid) {
                    view.setData(mAccount!!)
                }
            })
        }
        mWebsites = MiqiDbHelper.getWebsites(realm)
    }

    override fun addLoginWay(accountName: String) {
        if (mAccount != null) {
            val disposable = MiqiDbHelper.addLoginWay(realm, mAccount!!.id, accountName)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResultConsumer(), ThrowableConsumer())
            addDisposable(disposable)
        }
    }

    override fun addLoginWay(accountId: Long) {
        if (mAccount != null) {
            val disposable = MiqiDbHelper.addLoginWay(realm, mAccount!!.id, accountId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResultConsumer(), ThrowableConsumer())
            addDisposable(disposable)
        }
    }
}