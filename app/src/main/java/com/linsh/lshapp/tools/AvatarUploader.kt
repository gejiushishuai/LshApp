package com.linsh.lshapp.tools

import com.linsh.lshapp.model.result.UploadAvatarResult
import com.linsh.lshapp.task.network.UrlConnector
import com.linsh.utilseverywhere.ImageUtils
import com.linsh.utilseverywhere.LogUtils
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2018/01/05
 *    desc   :
 * </pre>
 */
object AvatarUploader {

    fun uploadAvatar(name: String, avatarFile: File?): Flowable<UploadAvatarResult> {
        return if (avatarFile == null) Flowable.error(Throwable(""))
        else {
            val avatarName = NameTool.getAvatarName(name)
            val thumbName = NameTool.getAvatarThumbName(avatarName)
            val thumbFile = LshFileFactory.getUploadThumbFile(LshIdTools.getTimeId())
            val result = UploadAvatarResult("", "")
            Flowable.just("")
                    .subscribeOn(Schedulers.io())
                    // 生成缩略图
                    .flatMap { _ ->
                        LogUtils.i("生成缩略图")
                        // 宽高 256*256  最大尺寸 50Kb
                        val success = ImageUtils.compressImage(avatarFile, thumbFile, 256, 256, 50)
                        if (success) {
                            return@flatMap Flowable.just("")
                        }
                        return@flatMap Flowable.error<String>(Throwable("生成缩略图失败!"))
                    }
                    // 上传缩略图
                    .flatMap { _ ->
                        LogUtils.i("上传缩略图")
                        return@flatMap UrlConnector.uploadThumb(thumbName, thumbFile)
                                .map<Unit> { uploadInfoHttpInfo ->
                                    result.thumbUrl = uploadInfoHttpInfo.data.source_url
                                }
                    }
                    // 上传头像
                    .flatMap { _ ->
                        LogUtils.i("上传头像")
                        return@flatMap UrlConnector.uploadAvatar(avatarName, avatarFile)
                                .map<Unit> { uploadInfoHttpInfo ->
                                    result.url = uploadInfoHttpInfo.data.source_url
                                }
                    }
                    .flatMap { _ ->
                        return@flatMap Flowable.just(result)
                    }

        }
    }

    fun uploadThumb(name: String, avatarFile: File?): Flowable<String> {
        return if (avatarFile == null) Flowable.error(Throwable(""))
        else {
            val avatarName = NameTool.getAvatarName(name)
            val thumbName = NameTool.getAvatarThumbName(avatarName)
            val thumbFile = LshFileFactory.getUploadThumbFile(LshIdTools.getTimeId())
            Flowable.just("")
                    .subscribeOn(Schedulers.io())
                    // 生成缩略图
                    .flatMap { _ ->
                        LogUtils.i("生成缩略图")
                        // 宽高 256*256  最大尺寸 50Kb
                        val success = ImageUtils.compressImage(avatarFile, thumbFile, 256, 256, 50)
                        if (success) {
                            return@flatMap Flowable.just("")
                        }
                        return@flatMap Flowable.error<String>(Throwable("生成缩略图失败!"))
                    }
                    // 上传缩略图
                    .flatMap { _ ->
                        LogUtils.i("上传缩略图")
                        return@flatMap UrlConnector.uploadThumb(thumbName, thumbFile)
                                .map<String> { uploadInfoHttpInfo ->
                                    return@map uploadInfoHttpInfo.data.source_url
                                }
                    }
        }
    }
}