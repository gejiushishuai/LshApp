package com.linsh.lshapp.base;

import android.content.Context;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public class BaseViewActivity extends BaseActivity implements BaseView {

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
