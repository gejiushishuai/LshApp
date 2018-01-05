package com.linsh.lshapp.model.bean;

import android.widget.ImageView;

import com.linsh.utilseverywhere.RegexUtils;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.db.shiyi.ImageUrl;
import com.linsh.lshapp.tools.ImageTools;
import com.linsh.views.album.Image;

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
        if (StringUtils.notEmpty(url) && RegexUtils.isURL(url)) {
            ImageTools.setImage(imageView, url);
        } else {
            ImageTools.setImage(imageView, R.drawable.ic_error_default);
        }
    }
}
