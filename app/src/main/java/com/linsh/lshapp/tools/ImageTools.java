package com.linsh.lshapp.tools;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshStringUtils;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class ImageTools {


    public static void setImage(ImageView imageView, String url) {
        if (imageView == null || LshStringUtils.isEmpty(url)) return;

        try {
            Glide.with(LshApplicationUtils.getContext()).load(url).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
