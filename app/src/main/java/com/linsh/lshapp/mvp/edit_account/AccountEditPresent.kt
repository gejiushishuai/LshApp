package com.linsh.lshapp.mvp.edit_account

import com.linsh.lshapp.base.RealmPresenterImpl
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.model.bean.db.miqi.AccountAvatar
import com.linsh.lshapp.model.bean.db.miqi.Website
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
class AccountEditPresent : RealmPresenterImpl<AccountEditContract.View>(), AccountEditContract.Presenter {

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

    override fun getWebsites(): List<Website>? {
        return if (mWebsites.isValid) mWebsites else null
    }

    override fun saveAccount(name: String, website: String, avatar: AccountAvatar?) {
        val account = if (mAccount != null) realm.copyFromRealm(mAccount!!) else Account()
        account.name = name
        if (account.website?.name != website) {
            account.website = Website(website)
        }
        account.avatar = avatar
        val disposable = MiqiDbHelper.saveAccount(realm, account)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ _ -> view.finishActivity() })
        addDisposable(disposable)
    }

    override fun addWebsite(website: String) {
        val disposable = MiqiDbHelper.createWebsite(realm, Website(website))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        addDisposable(disposable)
    }
}