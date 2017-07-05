package com.linsh.lshapp.model.transfer;

import android.graphics.BitmapFactory;

import com.linsh.lshapp.tools.LshRxUtils;

import java.io.File;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Senh Linsh on 17/5/19.
 */

public class BitmapTransfer implements Function<ResponseBody, Flowable<File>> {

    private File destFile;

    public BitmapTransfer(String destFile) {
        this(new File(destFile));
    }

    public BitmapTransfer(File destFile) {
        this.destFile = destFile;
    }

    @Override
    public Flowable<File> apply(ResponseBody responseBody) {
        return LshRxUtils.create(emitter -> {
            BitmapFactory.decodeStream(responseBody.byteStream());
        });
    }
}
