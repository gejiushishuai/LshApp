package com.linsh.lshapp.lib.qcloud;


import android.support.annotation.NonNull;

import com.linsh.lshapp.common.QCloudConfig;
import com.linsh.lshapp.lib.qcloud.sign.Credentials;
import com.linsh.lshapp.lib.qcloud.sign.Sign;

/**
 * Created by Senh Linsh on 16/12/28.
 */
public class QcloudSignCreater {

    public static String getPeriodSign(String cosPath) {
        try {
            return Sign.getPeriodEffectiveSign(QCloudConfig.BUCKET_NAME, cosPath, getCred(), getExpiredTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getOnceSign(String cosPath) {
        try {
            return Sign.getOneEffectiveSign(QCloudConfig.BUCKET_NAME, cosPath, getCred());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDownLoadSign(String cosPath) {
        try {
            return Sign.getDownLoadSign(QCloudConfig.BUCKET_NAME, cosPath, getCred(), getExpiredTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static long getExpiredTime() {
        return System.currentTimeMillis() / 1000 + 60 * 60;
    }

    @NonNull
    private static Credentials getCred() {
        return new Credentials(QCloudConfig.APP_ID, QCloudConfig.SECRET_ID, QCloudConfig.SECRET_KEY);
    }
}
