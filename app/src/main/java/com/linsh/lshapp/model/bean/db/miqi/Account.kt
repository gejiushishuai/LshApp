package com.linsh.lshapp.model.bean.db.miqi

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/22
 *    desc   :
 * </pre>
 */
open class Account : RealmObject {

    @PrimaryKey
    var id: Long = 0
    var website: Website? = null
    var name: String? = null
    var avatar: String? = null
    var loginName: String? = null
    var loginAccounts: RealmList<Account>? = null

    constructor()

    constructor(id: Long, website: Website, name: String, avatar: String? = null,
                loginName: String? = null, loginAccounts: RealmList<Account>? = null) {
        this.id = id
        this.website = website
        this.name = name
        this.avatar = avatar
        this.loginName = loginName
        this.loginAccounts = loginAccounts
    }
}