package com.linsh.lshapp.model.bean.db;

import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/6/9.
 */

public class ImageUrl extends RealmObject {

    private String url;
    private String thumbUrl;

    public ImageUrl() {
    }

    public ImageUrl(String sourceUrl, String thumbUrl) {
        this.url = sourceUrl;
        this.thumbUrl = thumbUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
}
