package com.linsh.lshapp.task.network;

import com.linsh.lshapp.common.QCloudConfig;
import com.linsh.lshapp.lib.qcloud.QcloudSignCreater;
import com.linsh.lshapp.model.bean.http.CreateDirInfo;
import com.linsh.lshapp.model.bean.http.HttpInfo;
import com.linsh.lshapp.model.bean.http.NoDataInfo;
import com.linsh.lshapp.model.bean.http.UploadInfo;
import com.linsh.lshapp.model.transfer.FileTransfer;
import com.linsh.lshapp.task.network.api.DirService;
import com.linsh.lshapp.task.network.api.FileService;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshutils.module.unit.FileSize;
import com.linsh.lshutils.utils.Basic.LshFileUtils;
import com.linsh.lshutils.utils.LshTimeUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;

import static com.linsh.lshapp.task.network.RetrofitHelper.createApi;

/**
 * Created by Senh Linsh on 17/6/2.
 */

public class UrlConnector {

    public static Observable<HttpInfo<UploadInfo>> uploadRealmData() {
        File file = LshFileFactory.getRealmFile();
        String fileName = "shiyi_" + LshTimeUtils.getCurrentTimeStringEN() + ".realm";
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return uploadFile("shiyi/realm", fileName, file);
    }

    public static Observable<HttpInfo<UploadInfo>> uploadAvatar(String fileName, File file) {
        RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return uploadFile("avatar", fileName, file);
    }

    public static Observable<HttpInfo<UploadInfo>> uploadFile(String dirName, String fileName, File file) {
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

    public static Observable<Float> downloadFile(String dirName, String fileName, File destFile) {
        return createApi(FileService.class, QCloudConfig.HOST_DOWNLOAD)
                .download(QcloudSignCreater.getPeriodSign(dirName + "/" + fileName), dirName, fileName)
                .flatMap(new FileTransfer(destFile) {
                    @Override
                    public void inProgress(float progress, long total) {
                        super.inProgress(progress, total);
                    }
                });
    }

    public static Observable<NoDataInfo> deleteFile(String dirName, String fileName) {
        return createApi(FileService.class, QCloudConfig.HOST_DOWNLOAD)
                .delete(QcloudSignCreater.getOnceSign(dirName + "/" + fileName), dirName, fileName, "delete");
    }

    public static Observable<HttpInfo<CreateDirInfo>> createDir(String dirName) {
        return createApi(DirService.class, QCloudConfig.HOST_DOWNLOAD)
                .create(QcloudSignCreater.getPeriodSign(dirName), dirName, "create");
    }

    public static Observable<NoDataInfo> deleteDir(String dirName) {
        return createApi(DirService.class, QCloudConfig.HOST_DOWNLOAD)
                .delete(QcloudSignCreater.getOnceSign(dirName), dirName, "delete");
    }
}
