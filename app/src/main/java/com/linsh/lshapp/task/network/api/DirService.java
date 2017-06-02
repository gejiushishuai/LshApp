package com.linsh.lshapp.task.network.api;

import com.linsh.lshapp.common.QCloudConfig;
import com.linsh.lshapp.model.bean.http.CreateDirInfo;
import com.linsh.lshapp.model.bean.http.HttpInfo;
import com.linsh.lshapp.model.bean.http.NoDataInfo;

import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Senh Linsh on 17/6/2.
 */

public interface DirService {

    @POST(QCloudConfig.URL_PREFIX + "/{dirName}/")
    Observable<HttpInfo<CreateDirInfo>> create(@Header("Authorization") String auth, @Path("dirName") String dirName, @Query("op") String op);

    @POST(QCloudConfig.URL_PREFIX + "/{dirName}/")
    Observable<NoDataInfo> delete(@Header("Authorization") String auth, @Path("dirName") String dirName, @Query("op") String op);
}
