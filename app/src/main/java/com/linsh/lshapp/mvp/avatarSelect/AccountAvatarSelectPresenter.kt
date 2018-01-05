package com.linsh.lshapp.mvp.avatarSelect

import com.google.gson.Gson
import com.linsh.lshapp.model.bean.db.miqi.AccountAvatar
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

class AccountAvatarSelectPresenter : AvatarSelectPresenter(), AvatarSelectContract.Presenter {

    private lateinit var avatars: RealmResults<AccountAvatar>

    override fun attachView() {
        avatars = MiqiDbHelper.getAccountAvatars(realm)
        avatars.addChangeListener { _ ->
            if (avatars.isValid) {
                view.setData(avatars)
            }
        }
    }

    fun saveAvatar(image: Image) {
        if (image is AccountAvatar) {
            view.showLoadingDialog()
            val disposable = MiqiDbHelper.saveAccountAvatar(realm, image)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnTerminate { view.dismissLoadingDialog() }
                    .subscribe(ResultConsumer(), ThrowableConsumer())
            addDisposable(disposable)
        }
    }

    override fun addAvatar(file: File) {
        view.showLoadingDialog()
        val disposable = AvatarUploader.uploadAvatar("website", file)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { result ->
                    return@flatMap MiqiDbHelper.saveAccountAvatar(realm, AccountAvatar(result.url, result.thumbUrl))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate { view.dismissLoadingDialog() }
                .subscribe(ResultConsumer(), ThrowableConsumer())
        addDisposable(disposable)
    }

    override fun getJson(image: Image?): String? {
        if (image is AccountAvatar) {
            return Gson().toJson(realm.copyFromRealm(image))
        }
        return null
    }
}