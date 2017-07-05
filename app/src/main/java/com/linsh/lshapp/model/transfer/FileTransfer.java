package com.linsh.lshapp.model.transfer;

import com.linsh.lshutils.utils.Basic.LshIOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Senh Linsh on 17/5/19.
 */

public class FileTransfer implements Function<ResponseBody, Flowable<File>> {

    private File destFile;

    public FileTransfer(String destFile) {
        this(new File(destFile));
    }

    public FileTransfer(File destFile) {
        this.destFile = destFile;
    }

    @Override
    public Flowable<File> apply(ResponseBody responseBody) throws Exception {
        return Flowable.create(emitter -> {
            try {
                saveFile(responseBody, emitter);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        }, BackpressureStrategy.ERROR);
    }

    private void saveFile(ResponseBody response, FlowableEmitter<? super File> emitter) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.byteStream();

            if (destFile == null) {
                throw new RuntimeException("没有传入文件");
            }
            destFile.getParentFile().mkdirs();

            fos = new FileOutputStream(destFile);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            emitter.onNext(destFile);
        } finally {
            LshIOUtils.close(response, is, fos);
        }
    }
}