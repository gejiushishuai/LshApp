package com.linsh.lshapp.task.db

import com.linsh.lshapp.model.action.AsyncTransaction
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.model.bean.db.miqi.AccountAvatar
import com.linsh.lshapp.model.bean.db.miqi.WebAvatar
import com.linsh.lshapp.model.bean.db.miqi.Website
import com.linsh.lshapp.model.result.Result
import com.linsh.lshapp.tools.LshRxUtils
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/23
 *    desc   :
 * </pre>
 */

object MiqiDbHelper {


    fun getAccounts(realm: Realm): RealmResults<Account> {
        return realm.where(Account::class.java).sort("id").findAllAsync()
    }

    fun getWebsites(realm: Realm): RealmResults<Website> {
        return realm.where(Website::class.java).findAllAsync()
    }

    fun getAccount(realm: Realm, id: Long): Account {
        return realm.where(Account::class.java).equalTo("id", id).findFirstAsync()
    }

    fun getWebsiteAvatars(realm: Realm): RealmResults<WebAvatar> {
        return realm.where(WebAvatar::class.java).findAllAsync()
    }

    fun getAccountAvatars(realm: Realm): RealmResults<AccountAvatar> {
        return realm.where(AccountAvatar::class.java).findAllAsync()
    }

    fun updateOrCreateAccount(realm: Realm, account: Account): Flowable<Result> {
        return LshRxUtils.getAsyncTransactionFlowable(realm, object : AsyncTransaction<Result>() {
            override fun execute(realm: Realm, emitter: FlowableEmitter<in Result>) {
                if (account.id <= 0) {
                    val maxId = realm.where(Account::class.java).max("id")
                    account.id = maxId?.toLong() ?: 0 + 1
                }
                realm.copyToRealmOrUpdate(account)
                emitter.onNext(Result())
            }
        })
    }

    fun saveAccount(realm: Realm, account: Account): Flowable<Result> {
        return LshRxUtils.getAsyncTransactionFlowable(realm, object : AsyncTransaction<Result>() {
            override fun execute(realm: Realm, emitter: FlowableEmitter<in Result>) {
                // 网站 (网站传入的只有名字, 需要在这里进行查询)
                val website = account.website ?: Website("未知")
                var realmWebsite = realm.where(Website::class.java).equalTo("name", website.name).findFirst()
                if (realmWebsite == null) {
                    realmWebsite = realm.copyToRealm(website)
                }
                account.website = realmWebsite!!
                if (account.id <= 0L) {
                    val maxId = realm.where(Account::class.java).max("id")
                    account.id = (maxId?.toLong() ?: 0) + 1
                }
                realm.copyToRealmOrUpdate(account)
                emitter.onNext(Result())
            }
        })
    }

    fun createWebsite(realm: Realm, website: Website): Flowable<Result> {
        return LshRxUtils.getAsyncTransactionFlowable(realm, object : AsyncTransaction<Result>() {
            override fun execute(realm: Realm, emitter: FlowableEmitter<in Result>) {
                val realmWebsite = realm.where(Website::class.java).equalTo("name", website.name).findFirst()
                if (realmWebsite == null) {
                    realm.copyToRealm(website)
                    emitter.onNext(Result())
                } else {
                    emitter.onNext(Result("该网站已存在"))
                }
            }
        })
    }

    fun saveWebsite(realm: Realm, website: Website): Flowable<Result> {
        return LshRxUtils.getAsyncTransactionFlowable(realm, object : AsyncTransaction<Result>() {
            override fun execute(realm: Realm, emitter: FlowableEmitter<in Result>) {
                realm.copyToRealmOrUpdate(website)
                emitter.onNext(Result())
            }
        })
    }

    fun addLoginWay(realm: Realm, accountId: Long, otherId: Long): Flowable<Result> {
        return LshRxUtils.getAsyncTransactionFlowable(realm, object : AsyncTransaction<Result>() {
            override fun execute(realm: Realm, emitter: FlowableEmitter<in Result>) {
                val account = realm.where(Account::class.java).equalTo("id", accountId).findFirst()
                if (account != null) {
                    val addAccount = realm.where(Account::class.java).equalTo("id", otherId).findFirst()
                    if (addAccount != null) {
                        var loginWays = account.loginAccounts
                        if (loginWays == null) {
                            loginWays = RealmList(addAccount)
                            account.loginAccounts = loginWays
                        } else {
                            loginWays.add(addAccount)
                        }
                        emitter.onNext(Result())
                    } else {
                        emitter.onNext(Result("添加失败, 没有找到该帐号"))
                    }
                } else {
                    emitter.onNext(Result("无效帐号"))
                }
            }
        })
    }

    fun addLoginWay(realm: Realm, accountId: Long, newLoginName: String): Flowable<Result> {
        return LshRxUtils.getAsyncTransactionFlowable(realm, object : AsyncTransaction<Result>() {
            override fun execute(realm: Realm, emitter: FlowableEmitter<in Result>) {

                val account = realm.where(Account::class.java).equalTo("id", accountId).findFirst()
                if (account != null) {
                    if (account.loginName?.isNotEmpty() == true) {
                        val loginName = account.loginName!!
                        if (!loginName.matches(Regex("(.+\\+\\+)*$newLoginName(\\+\\+.+)*"))) {
                            if (loginName.contains(Regex("::"))) {
                                account.loginName = loginName.replace("::", "++$newLoginName::")
                            } else {
                                account.loginName += "++$newLoginName"
                            }
                        } else {
                            emitter.onNext(Result("该用户名已存在"))
                        }
                    } else {
                        account.loginName = newLoginName
                    }
                } else {
                    emitter.onNext(Result("无效帐号"))
                }
            }
        })
    }

    fun saveWebAvatar(realm: Realm, webAvatar: WebAvatar): Flowable<Result> {
        val avatar = if (!webAvatar.isManaged) webAvatar else realm.copyFromRealm(webAvatar)
        return LshRxUtils.getAsyncTransactionFlowable(realm, object : AsyncTransaction<Result>() {
            override fun execute(realm: Realm, emitter: FlowableEmitter<in Result>) {

                realm.copyToRealmOrUpdate(avatar)
                emitter.onNext(Result())
            }
        })
    }

    fun saveAccountAvatar(realm: Realm, accountAvatar: AccountAvatar): Flowable<Result> {
        val avatar = if (!accountAvatar.isManaged) accountAvatar else realm.copyFromRealm(accountAvatar)
        return LshRxUtils.getAsyncTransactionFlowable(realm, object : AsyncTransaction<Result>() {
            override fun execute(realm: Realm, emitter: FlowableEmitter<in Result>) {

                realm.copyToRealmOrUpdate(avatar)
                emitter.onNext(Result())
            }
        })
    }
}
