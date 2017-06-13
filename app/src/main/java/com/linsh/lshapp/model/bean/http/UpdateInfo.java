package com.linsh.lshapp.model.bean.http;

import java.util.Map;

/**
 * Created by Senh Linsh on 17/6/12.
 */

public class UpdateInfo {

    public ApkBean apk;
    public Map<String, ApkBean> patchs;

    public static class ApkBean {
        public boolean mandatory;
        public String version;
        public String url;
    }
}
