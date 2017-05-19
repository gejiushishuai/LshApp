package com.linsh.lshapp.model.transfer;

import android.graphics.BitmapFactory;

import java.io.File;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Senh Linsh on 17/5/19.
 */

public class BitmapTransfer implements Func1<ResponseBody, Observable<File>> {

    private File destFile;

    public BitmapTransfer(String destFile) {
        this(new File(destFile));
    }

    public BitmapTransfer(File destFile) {
        this.destFile = destFile;
    }

    @Override
    public Observable<File> call(ResponseBody responseBody) {
        return Observable.create(subscriber -> {
            BitmapFactory.decodeStream(responseBody.byteStream());
        });
    }
}
