package com.linsh.lshapp.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.linsh.lshapp.R;

/**
 * Created by linsh on 17/2/2.
 */

public abstract class BaseToolbarActivity<T extends BaseContract.BasePresenter> extends BaseViewActivity<T> {

    private Toolbar mToolbar;

    protected abstract String getToolbarTitle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化ToolBar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(getToolbarTitle());
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = View.inflate(this, R.layout.activity_base_toolbar, null);
        ViewGroup rootView = (ViewGroup) view.findViewById(R.id.cl_shiyi_base_other_root);
        View inflate = View.inflate(this, layoutResID, null);
        rootView.addView(inflate);
        setContentView(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setToolbarTitle(String title) {
        mToolbar.setTitle(title);
    }

    protected Toolbar getToolbar() {
        return mToolbar;
    }
}
