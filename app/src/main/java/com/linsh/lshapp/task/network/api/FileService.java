package com.linsh.lshapp.task.network.api;


import com.linsh.lshapp.common.QCloudConfig;
import com.linsh.lshapp.model.bean.http.HttpInfo;
import com.linsh.lshapp.model.bean.http.NoDataInfo;
import com.linsh.lshapp.model.bean.http.UploadInfo;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Senh Linsh on 17/5/18.
 */

public interface FileService {

    /**
     * @param op         "upload"
     * @param insertOnly 同名文件覆盖选项, 0 覆盖, 1 不覆盖
     */
    @Multipart
    @POST(QCloudConfig.URL_PREFIX + "/{dirName}/{fileName}?op=upload")
    Flowable<HttpInfo<UploadInfo>> upload(@Header("Authorization") String auth, @Path("dirName") String dirName, @Path("fileName") String fileName,
                                          @Query("op") String op, @Query("insertOnly") int insertOnly, @Part("filecontent") RequestBody file);

    @POST(QCloudConfig.URL_PREFIX + "/{dirName}/{fileName}")
    Flowable<NoDataInfo> delete(@Header("Authorization") String auth, @Path("dirName") String dirName, @Path("fileName") String fileName,
                                  @Query("op") String op);

    @GET("{dirName}/{fileName}")
    Flowable<ResponseBody> download(@Header("Authorization") String auth, @Path("dirName") String dirName, @Path("fileName") String fileName);
}
