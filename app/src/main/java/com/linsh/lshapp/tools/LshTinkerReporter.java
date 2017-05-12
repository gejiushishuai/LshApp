package com.linsh.lshapp.tools;

import android.util.Log;

import com.linsh.lshapp.lib.tinker.reporter.TinkerReport;

/**
 * Created by Senh Linsh on 17/5/12.
 */
public class LshTinkerReporter implements TinkerReport.Reporter {


    @Override
    public void onReport(int key, String keyName, String detail) {
        Log.i("LshLog", "LshTinkerReporter: onReport: " + String.format("key = %s, keyName = %s, detail = %s", key, keyName, detail));
    }

    @Override
    public void onReport(String message) {
        Log.i("LshLog", "LshTinkerReporter: onReport: message = " + message);
    }
}
