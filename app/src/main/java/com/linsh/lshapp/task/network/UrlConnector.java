package com.linsh.lshapp.task.network;

import com.linsh.lshapp.BuildConfig;
import com.linsh.lshapp.common.QCloudConfig;
import com.linsh.lshapp.lib.qcloud.QcloudSignCreater;
import com.linsh.lshapp.model.bean.http.CreateDirInfo;
import com.linsh.lshapp.model.bean.http.HttpInfo;
import com.linsh.lshapp.model.bean.http.NoDataInfo;
import com.linsh.lshapp.model.bean.http.UpdateInfo;
import com.linsh.lshapp.model.bean.http.UploadInfo;
import com.linsh.lshapp.model.throwabes.CustomThrowable;
import com.linsh.lshapp.model.transfer.FileTransfer;
import com.linsh.lshapp.task.network.api.CommonApi;
import com.linsh.lshapp.task.network.api.DirService;
import com.linsh.lshapp.task.network.api.FileService;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshutils.module.unit.FileSize;
import com.linsh.lshutils.utils.Basic.LshFileUtils;
import com.linsh.lshutils.utils.LshTimeUtils;

import java.io.File;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Senh Linsh on 17/6/2.
 */

public class UrlConnector {

    private static Flowable<HttpInfo<UploadInfo>> uploadFile(String dirName, String fileName, File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        if (LshFileUtils.getFileSize(file, FileSize.MB) < 20) {
            return RetrofitHelper.createApi(FileService.class, QCloudConfig.HOST)
                    .upload(QcloudSignCreater.getPeriodSign(dirName + "/" + fileName), dirName, fileName, "upload", 1, requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            // TODO: 17/6/2  分片上传
            return Flowable.create(new FlowableOnSubscribe<HttpInfo<UploadInfo>>() {
                @Override
                public void subscribe(FlowableEmitter<HttpInfo<UploadInfo>> emitter) throws Exception {
                    emitter.onError(new CustomThrowable("暂不支持上传 20M 以上文件"));
                }
            }, BackpressureStrategy.ERROR);
        }
    }

    private static Flowable<ResponseBody> downloadFile(String dirName, String fileName) {
        return RetrofitHelper.createApi(FileService.class, QCloudConfig.HOST_DOWNLOAD)
                .download(QcloudSignCreater.getPeriodSign(dirName + "/" + fileName), dirName, fileName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Flowable<NoDataInfo> deleteFile(String dirName, String fileName) {
        return RetrofitHelper.createApi(FileService.class, QCloudConfig.HOST_DOWNLOAD)
                .delete(QcloudSignCreater.getOnceSign(dirName + "/" + fileName), dirName, fileName, "deleteFile")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Flowable<HttpInfo<CreateDirInfo>> createDir(String dirName) {
        return RetrofitHelper.createApi(DirService.class, QCloudConfig.HOST_DOWNLOAD)
                .create(QcloudSignCreater.getPeriodSign(dirName), dirName, "create")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Flowable<NoDataInfo> deleteDir(String dirName) {
        return RetrofitHelper.createApi(DirService.class, QCloudConfig.HOST_DOWNLOAD)
                .delete(QcloudSignCreater.getOnceSign(dirName), dirName, "deleteFile")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<HttpInfo<UploadInfo>> uploadRealmData() {
        File file = LshFileFactory.getRealmFile();
        String time = LshTimeUtils.getTimeString(System.currentTimeMillis(), "yyyyMMdd_HHmmss");
        String fileName = "shiyi_" + time + (BuildConfig.DEBUG ? "_debug" : "") + ".realm";
        String dirName = BuildConfig.DEBUG ? "shiyi/realm/debug" : "shiyi/realm";
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return uploadFile(dirName, fileName, file);
    }

    public static Flowable<HttpInfo<UploadInfo>> uploadAvatar(String fileName, File file) {
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
        String dirName = BuildConfig.DEBUG ? "avatar/debug" : "avatar";
        return uploadFile(dirName, fileName, file);
    }

    public static Flowable<HttpInfo<UploadInfo>> uploadThumb(String thumbName, File file) {
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
        String dirName = BuildConfig.DEBUG ? "thumb/debug" : "thumb";
        return uploadFile(dirName, thumbName, file);
    }

    public static Flowable<HttpInfo<UpdateInfo>> checkUpdate() {
        return RetrofitHelper.createApi(CommonApi.class, QCloudConfig.HOST_DOWNLOAD)
                .update(QcloudSignCreater.getDownLoadSign("json/update.json"), "json", "update.json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Flowable<File> downloadApk(String url, File destFile) {
        return downloadFile("file/apk", getFileNameFromUrl(url))
                .flatMap(new FileTransfer(destFile));
    }

    public static String getFileNameFromUrl(String url) {
        return url.replaceFirst("https?://.+/", "");
    }

    public static Flowable<File> downloadPatch(String url) {
        String fileName = getFileNameFromUrl(url);
        return downloadFile("file/patch", fileName)
                .flatMap(new FileTransfer(LshFileFactory.getPatchFile(fileName)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
