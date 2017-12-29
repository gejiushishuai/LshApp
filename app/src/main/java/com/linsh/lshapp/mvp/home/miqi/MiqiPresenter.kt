package com.linsh.lshapp.mvp.home.miqi

import com.linsh.lshapp.base.RealmPresenterImpl
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.task.db.MiqiDbHelper
import io.realm.RealmResults

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/22
 *    desc   :
 * </pre>
 */
class MiqiPresenter : RealmPresenterImpl<MiqiContract.View>(), MiqiContract.Presenter {

    lateinit var mAccounts: RealmResults<Account>

    override fun attachView() {
        mAccounts = MiqiDbHelper.getAccounts(realm)
        mAccounts.addChangeListener({ _ ->
            if (mAccounts.isValid) {
                view.setData(mAccounts)
            }
        })
    }

}