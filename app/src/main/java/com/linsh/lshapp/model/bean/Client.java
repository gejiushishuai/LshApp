package com.linsh.lshapp.model.bean;

import java.io.Serializable;

public enum Client implements Serializable {

    DianXinYYT("电信营业厅", "签到"), CnMobile("中国移动", "签到");

    private final String mAppName;
    private final String mAction;

    Client(String appName, String action) {
        mAppName = appName;
        mAction = action;
    }

    public String getAppName() {
        return mAppName;
    }

    public String getAction() {
        return mAction;
    }
}