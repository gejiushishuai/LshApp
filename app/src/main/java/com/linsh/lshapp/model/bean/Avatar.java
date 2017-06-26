package com.linsh.lshapp.model.bean;

import android.widget.ImageView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.db.ImageUrl;
import com.linsh.lshapp.tools.ImageTools;
import com.linsh.lshutils.module.Image;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshRegexUtils;

/**
 * Created by Senh Linsh on 17/6/26.
 */

public class Avatar implements Image {

    public ImageUrl imageUrl;

    public Avatar(ImageUrl imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public void setImage(ImageView imageView) {
        String url = imageUrl.getThumbUrl();
        if (LshStringUtils.notEmpty(url) && LshRegexUtils.isURL(url)) {
            ImageTools.setImage(imageView, url);
        } else {
            ImageTools.setImage(imageView, R.drawable.ic_default_image);
        }
    }
}
