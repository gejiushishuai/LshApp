package com.linsh.lshapp.tools;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.linsh.lshapp.lib.qcloud.QcloudSignCreater;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshStringUtils;

import java.io.File;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class ImageTools {


    public static void setImage(ImageView imageView, String url) {
        if (imageView == null || LshStringUtils.isEmpty(url)) return;

        try {
            LazyHeaders.Builder builder = new LazyHeaders.Builder()
                    .addHeader("Authorization", QcloudSignCreater.getDownLoadSign(getCosPathFromUrl(url)));
            GlideUrl glideUrl = new GlideUrl(url, builder.build());
            Glide.with(LshApplicationUtils.getContext()).load(glideUrl).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setImage(ImageView imageView, File file) {
        if (imageView == null || file == null || !file.exists()) return;

        try {
            Glide.with(LshApplicationUtils.getContext()).load(file).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCosPathFromUrl(String downloadUrl) {
        // "source_url": "http://accesslog-10055004.cossh.myqcloud.com/testfolder/111.txt"
        return downloadUrl.replaceFirst("https?://.+\\.com", "");
    }
}
