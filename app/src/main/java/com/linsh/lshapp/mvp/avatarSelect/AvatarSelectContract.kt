package com.linsh.lshapp.mvp.avatarSelect

import com.linsh.lshapp.base.BaseContract
import com.linsh.views.album.Image
import java.io.File

/**
 * Created by Senh Linsh on 17/4/25.
 */

interface AvatarSelectContract {

    interface View : BaseContract.BaseView {
        fun setData(avatars: List<Image>)
    }

    interface Presenter : BaseContract.BasePresenter<View> {
        fun addAvatar(file: File)
        fun getJson(image: Image?): String?
    }
}
