package com.linsh.lshapp.tools;

import android.app.Activity;

import com.linsh.lshutils.handler.LshCrashHandler;
import com.linsh.lshutils.utils.LogPrinterUtils;

/**
 * Created by Senh Linsh on 17/6/15.
 */

public class CrashHandler extends LshCrashHandler {

    @Override
    protected void onCatchException(Thread thread, Throwable thr) {
        LogPrinterUtils.e(thr);
    }

    @Override
    protected boolean isHandleByDefaultHandler(Thread thread, Throwable thr) {
        return true;
    }

    @Override
    protected Class<? extends Activity> onRestartAppIfNeeded() {
        return null;
    }
}
