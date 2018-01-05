package com.linsh.lshapp.tools;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.linsh.utilseverywhere.ContextUtils;
import com.linsh.utilseverywhere.RegexUtils;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.lshapp.R;
import com.linsh.lshapp.lib.qcloud.QcloudSignCreater;

import java.io.File;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class ImageTools {

    public static RequestManager getGlide() {
        return Glide.with(ContextUtils.get());
    }

    public static DrawableTypeRequest<GlideUrl> getGlide(String url) {
        LazyHeaders.Builder headerBuilder = new LazyHeaders.Builder()
                .addHeader("Authorization", QcloudSignCreater.getDownLoadSign(getCosPathFromUrl(url)));
        GlideUrl glideUrl = new GlideUrl(url, headerBuilder.build());

        return Glide.with(ContextUtils.get())
                .load(glideUrl);
    }

    public static void setThumb(ImageView imageView, String thumbUrl, String ImageUrl) {
        setThumb(imageView, thumbUrl, ImageUrl, 0, 0);
    }

    public static void setThumb(ImageView imageView, String thumbUrl, String ImageUrl, int placeholder, int error) {
        String url = StringUtils.isEmpty(thumbUrl) ? ImageUrl : thumbUrl;
        setImage(imageView, url, placeholder, error);
    }

    public static void setImage(ImageView imageView, String url) {
        setImage(imageView, url, 0, 0);
    }

    public static void setImage(ImageView imageView, String url, int placeholder) {
        setImage(imageView, url, placeholder, placeholder);
    }

    public static void setImage(ImageView imageView, String url, int placeholder, int error) {
        if (imageView == null) return;

        if (StringUtils.isEmpty(url)) {
            setImage(imageView, error);
            return;
        }

        try {
            LazyHeaders.Builder headerBuilder = new LazyHeaders.Builder()
                    .addHeader("Authorization", QcloudSignCreater.getDownLoadSign(getCosPathFromUrl(url)));
            GlideUrl glideUrl = new GlideUrl(url, headerBuilder.build());

            DrawableRequestBuilder<GlideUrl> builder =
                    Glide.with(ContextUtils.get())
                            .load(glideUrl)
                            .dontAnimate();
            if (placeholder > 0) {
                builder.placeholder(placeholder);
            }
            if (error > 0) {
                builder.error(error);
            }
            builder.into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            setImage(imageView, error);
        }
    }

    public static void setImage(ImageView imageView, File file) {
        if (imageView == null) return;

        if (file == null || !file.exists()) {
            setImage(imageView, R.drawable.ic_error_default);
            return;
        }

        try {
            Glide.with(ContextUtils.get()).load(file).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            setImage(imageView, R.drawable.ic_error_default);
        }
    }

    public static void setImage(ImageView imageView, int res) {
        Glide.with(ContextUtils.get()).load(res).into(imageView);
    }

    public static void setImage(ImageView imageView, Uri uri) {
        Glide.with(ContextUtils.get()).load(uri).into(imageView);
    }

    // 加载头像, 优先使用缩略图, 默认设置了 Loading 和 Error 的图片
    public static void loadAvatar(ImageView imageView, String thumbUrl, String ImageUrl) {
        setThumb(imageView, thumbUrl, ImageUrl, R.drawable.ic_avatar_loading, R.drawable.ic_contact);
    }

    public static String getCosPathFromUrl(String downloadUrl) {
        // "source_url": "http://accesslog-10055004.cossh.myqcloud.com/testfolder/111.txt"
        return downloadUrl.replaceFirst("https?://.+\\.com", "");
    }

    public static String getSignedUrl(String url) {
        return url + "?sign=" + QcloudSignCreater.getDownLoadSign(ImageTools.getCosPathFromUrl(url));
    }

    public static boolean isImageUrl(String url) {
        return StringUtils.notEmpty(url) && RegexUtils.isURL(url);
    }
}
