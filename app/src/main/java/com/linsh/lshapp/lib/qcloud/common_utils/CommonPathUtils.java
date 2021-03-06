package com.linsh.lshapp.lib.qcloud.common_utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CommonPathUtils {
    private static final String PATH_DELIMITER = "/";

    public static String encodeRemotePath(String urlPath) throws UnsupportedEncodingException {
        StringBuilder pathBuilder = new StringBuilder();
        String[] pathSegmentsArr = urlPath.split(PATH_DELIMITER);

        for (String pathSegment : pathSegmentsArr) {
            if (!pathSegment.isEmpty()) {
                try {
                    pathBuilder.append(PATH_DELIMITER).append(URLEncoder.encode(pathSegment, "UTF-8").replace("+", "%20"));
                } catch (UnsupportedEncodingException e) {
                    String errMsg = "Unsupported ecnode exception:" + e.toString();
                    Log.e("CommonPathUtils", errMsg);
                    throw e;
                }
            }
        }
        if (urlPath.endsWith(PATH_DELIMITER)) {
            pathBuilder.append(PATH_DELIMITER);
        }
        return pathBuilder.toString();
    }
}
