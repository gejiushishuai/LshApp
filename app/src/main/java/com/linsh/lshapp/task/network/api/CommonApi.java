package com.linsh.lshapp.task.network.api;

import com.linsh.lshapp.model.bean.http.HttpInfo;
import com.linsh.lshapp.model.bean.http.UpdateInfo;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by Senh Linsh on 17/6/12.
 */

public interface CommonApi {


    @GET("{dirName}/{fileName}")
    Flowable<HttpInfo<UpdateInfo>> update(@Header("Authorization") String auth, @Path("dirName") String dirName, @Path("fileName") String fileName);
}
