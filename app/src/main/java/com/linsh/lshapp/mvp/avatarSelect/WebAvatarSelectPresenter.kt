package com.linsh.lshapp.mvp.avatarSelect

import com.google.gson.Gson
import com.linsh.lshapp.model.bean.db.miqi.WebAvatar
import com.linsh.lshapp.model.consumer.ResultConsumer
import com.linsh.lshapp.model.consumer.ThrowableConsumer
import com.linsh.lshapp.task.db.MiqiDbHelper
import com.linsh.lshapp.tools.AvatarUploader
import com.linsh.views.album.Image
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.RealmResults
import java.io.File

/**
 * Created by Senh Linsh on 17/5/5.
 */

class WebAvatarSelectPresenter : AvatarSelectPresenter(), AvatarSelectContract.Presenter {
    private lateinit var avatars: RealmResults<WebAvatar>

    override fun attachView() {
        avatars = MiqiDbHelper.getWebsiteAvatars(realm)
        avatars.addChangeListener { _ ->
            if (avatars.isValid) {
                view.setData(avatars)
            }
        }
    }

    fun saveAvatar(image: Image) {
        if (image is WebAvatar) {
            val disposable = MiqiDbHelper.saveWebAvatar(realm, image)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ResultConsumer(), ThrowableConsumer())
            addDisposable(disposable)
        }
    }

    override fun addAvatar(file: File) {
        AvatarUploader.uploadThumb("website", file)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { thumb ->
                    return@flatMap MiqiDbHelper.saveWebAvatar(realm, WebAvatar(thumb))
                }
                .subscribe(ResultConsumer(), ThrowableConsumer())
    }

    override fun getJson(image: Image?): String? {
        if (image is WebAvatar) {
            return Gson().toJson(realm.copyFromRealm(image))
        }
        return null
    }
}