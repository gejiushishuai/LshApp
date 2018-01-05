package com.linsh.lshapp.model.bean.db.miqi

import android.widget.ImageView
import com.linsh.lshapp.R
import com.linsh.lshapp.tools.ImageTools
import com.linsh.views.album.Image
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2018/01/04
 *    desc   :
 * </pre>
 */
open class AccountAvatar(@PrimaryKey var url: String? = null, var thumbUrl: String? = null) : RealmObject(), Image {

    fun urlFirst(): String? {
        return url ?: thumbUrl
    }

    fun thumbFirst(): String? {
        return thumbUrl ?: url
    }

    override fun setImage(imageView: ImageView?) {
        ImageTools.setImage(imageView, thumbFirst(), 0, R.drawable.ic_error_default)
    }
}