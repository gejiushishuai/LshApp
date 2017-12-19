package com.linsh.lshapp.model.transfer;

import com.linsh.utilseverywhere.IOUtils;
import com.linsh.lshapp.tools.LshRxUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Senh Linsh on 17/5/19.
 */

public class FileProgressTransfer implements Function<ResponseBody, Flowable<Float>> {

    private File destFile;

    public FileProgressTransfer(String destFile) {
        this(new File(destFile));
    }

    public FileProgressTransfer(File destFile) {
        this.destFile = destFile;
    }

    @Override
    public Flowable<Float> apply(ResponseBody responseBody) {
        return LshRxUtils.create(emitter -> {
            try {
                saveFile(responseBody, emitter);
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        });
    }

    private void saveFile(ResponseBody response, FlowableEmitter<Float> emitter) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try {
            is = response.byteStream();
            final long total = response.contentLength();

            long sum = 0;

            if (destFile == null) {
                throw new RuntimeException("没有传入文件");
            }
            destFile.getParentFile().mkdirs();

            fos = new FileOutputStream(destFile);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                emitter.onNext(finalSum * 1.0f / total);
            }
            fos.flush();
        } finally {
            IOUtils.close(is, fos);
        }
    }
}
