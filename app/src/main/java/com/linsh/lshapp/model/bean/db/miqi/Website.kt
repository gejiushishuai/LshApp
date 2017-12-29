package com.linsh.lshapp.model.bean.db.miqi

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
open class Website : RealmObject {

    @PrimaryKey
    var name: String? = null
    var avatar: String? = null

    constructor()

    constructor(name: String, avatar: String? = null) : super() {
        this.name = name
        this.avatar = avatar
    }
}