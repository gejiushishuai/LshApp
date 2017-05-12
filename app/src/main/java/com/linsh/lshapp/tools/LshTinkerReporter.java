package com.linsh.lshapp.tools;

import com.linsh.lshapp.lib.tinker.reporter.TinkerReport;
import com.linsh.lshutils.utils.Basic.LshLogUtils;

/**
 * Created by Senh Linsh on 17/5/12.
 */
public class LshTinkerReporter implements TinkerReport.Reporter {


    @Override
    public void onReport(int key, String keyName, String detail) {
        LshLogUtils.i("onReport", String.format("key = %s, keyName = %s, detail = %s", key, keyName, detail));
    }

    @Override
    public void onReport(String message) {
        LshLogUtils.i("onReport", "message = " + message);
    }
}
