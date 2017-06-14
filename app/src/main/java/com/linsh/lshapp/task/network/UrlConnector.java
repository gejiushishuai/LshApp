package com.linsh.lshapp.task.network;

import com.linsh.lshapp.BuildConfig;
import com.linsh.lshapp.common.QCloudConfig;
import com.linsh.lshapp.lib.qcloud.QcloudSignCreater;
import com.linsh.lshapp.model.bean.http.CreateDirInfo;
import com.linsh.lshapp.model.bean.http.HttpInfo;
import com.linsh.lshapp.model.bean.http.NoDataInfo;
import com.linsh.lshapp.model.bean.http.UpdateInfo;
import com.linsh.lshapp.model.bean.http.UploadInfo;
import com.linsh.lshapp.model.transfer.FileTransfer;
import com.linsh.lshapp.task.network.api.CommonApi;
import com.linsh.lshapp.task.network.api.DirService;
import com.linsh.lshapp.task.network.api.FileService;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshutils.module.unit.FileSize;
import com.linsh.lshutils.tools.LshDownloadManager;
import com.linsh.lshutils.utils.Basic.LshFileUtils;
import com.linsh.lshutils.utils.LshTimeUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/6/2.
 */

public class UrlConnector {

    private static Observable<HttpInfo<UploadInfo>> uploadFile(String dirName, String fileName, File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        if (LshFileUtils.getFileSize(file, FileSize.MB) < 20) {
            return RetrofitHelper.createApi(FileService.class, QCloudConfig.HOST)
                    .upload(QcloudSignCreater.getPeriodSign(dirName + "/" + fileName), dirName, fileName, "upload", 1, requestBody);
        } else {
            // TODO: 17/6/2  分片上传
            return Observable.unsafeCreate(new Observable.OnSubscribe<HttpInfo<UploadInfo>>() {
                @Override
                public void call(Subscriber<? super HttpInfo<UploadInfo>> subscriber) {
                    subscriber.onError(new RuntimeException("暂不支持上传 20M 以上文件"));
                }
            });
        }
    }

    private static Observable<ResponseBody> downloadFile(String dirName, String fileName) {
        return RetrofitHelper.createApi(FileService.class, QCloudConfig.HOST_DOWNLOAD)
                .download(QcloudSignCreater.getPeriodSign(dirName + "/" + fileName), dirName, fileName);
    }

    private static Observable<NoDataInfo> deleteFile(String dirName, String fileName) {
        return RetrofitHelper.createApi(FileService.class, QCloudConfig.HOST_DOWNLOAD)
                .delete(QcloudSignCreater.getOnceSign(dirName + "/" + fileName), dirName, fileName, "delete");
    }

    private static Observable<HttpInfo<CreateDirInfo>> createDir(String dirName) {
        return RetrofitHelper.createApi(DirService.class, QCloudConfig.HOST_DOWNLOAD)
                .create(QcloudSignCreater.getPeriodSign(dirName), dirName, "create");
    }

    private static Observable<NoDataInfo> deleteDir(String dirName) {
        return RetrofitHelper.createApi(DirService.class, QCloudConfig.HOST_DOWNLOAD)
                .delete(QcloudSignCreater.getOnceSign(dirName), dirName, "delete");
    }

    public static Observable<HttpInfo<UploadInfo>> uploadRealmData() {
        File file = LshFileFactory.getRealmFile();
        String time = LshTimeUtils.getTimeString(System.currentTimeMillis(), "yyyyMMdd_HHmmss");
        String fileName = "shiyi_" + time + (BuildConfig.DEBUG ? "_debug" : "") + ".realm";
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return uploadFile("shiyi/realm", fileName, file);
    }

    public static Observable<HttpInfo<UploadInfo>> uploadAvatar(String fileName, File file) {
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return uploadFile("avatar", fileName, file);
    }

    public static Observable<HttpInfo<UploadInfo>> uploadThumb(String thumbName, File file) {
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return uploadFile("thumb", thumbName, file);
    }

    public static Observable<HttpInfo<UpdateInfo>> checkUpdate() {
        return RetrofitHelper.createApi(CommonApi.class, QCloudConfig.HOST_DOWNLOAD)
                .update(QcloudSignCreater.getDownLoadSign("json/update.json"), "json", "update.json")
                .subscribeOn(Schedulers.io());
    }

    public static Observable<File> downloadApk(String url, File destFile) {
        return downloadFile("file/apk", getFileNameFromUrl(url))
                .flatMap(new FileTransfer(destFile));
    }

    public static long downloadApk(LshDownloadManager manager, String url) {
        String fileName = getFileNameFromUrl(url);
        return manager.buildRequest(url, fileName)
                .addRequestHeader("Authorization", QcloudSignCreater.getDownLoadSign("file/apk/" + fileName))
                .download();
    }

    private static String getFileNameFromUrl(String url) {
        return url.replaceFirst("https?://.+/", "");
    }

    public static Observable<File> downloadPatch(String url) {
        String fileName = getFileNameFromUrl(url);
        return downloadFile("file/apk", fileName)
                .flatMap(new FileTransfer(LshFileFactory.getPatchFile(fileName)));
    }
}
