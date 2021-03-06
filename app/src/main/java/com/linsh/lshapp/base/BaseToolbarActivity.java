package com.linsh.lshapp.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.linsh.utilseverywhere.ResourceUtils;
import com.linsh.utilseverywhere.SystemUtils;
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
        // 设置沉浸状态栏
        SystemUtils.setTranslucentStatusBar(this, ResourceUtils.getColor(R.color.color_theme_dark_blue_pressed));
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
        // 填充子布局, 由子类返回布局id
        getLayoutInflater().inflate(getLayout(), (ViewGroup) view, true);
        setContentView(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
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
