package com.linsh.lshapp.model.bean.db.miqi

import android.widget.ImageView
import com.linsh.lshapp.R
import com.linsh.lshapp.tools.ImageTools
import com.linsh.views.album.Image
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2018/01/04
 *    desc   :
 * </pre>
 */
open class WebAvatar(@PrimaryKey var thumbUrl: String? = null) : RealmObject(), Image, Serializable {

    override fun setImage(imageView: ImageView?) {
        ImageTools.setImage(imageView, thumbUrl, 0, R.drawable.ic_error_default)
    }
}