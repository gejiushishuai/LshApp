package com.linsh.lshapp.mvp.home;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseViewActivity;
import com.linsh.lshapp.mvp.home.shiyi.ShiyiFragment;
import com.linsh.lshapp.tools.MainFragmentHelper;
import com.linsh.lshutils.utils.LshRecourseUtils;
import com.linsh.lshutils.utils.LshSystemUtils;

public class MainActivity extends BaseViewActivity<MainContract.MainPresenter> implements NavigationView.OnNavigationItemSelectedListener {

    private MainFragmentHelper mHomeFragmentHelper;
    private Toolbar mToolbar;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        LshSystemUtils.setTranslucentStatusBarWithInsertion(this, LshRecourseUtils.getColor(R.color.color_theme_dark_blue_pressed));
        // 初始化ToolBar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // 初始化菜单栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // 初始化NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 使用FragmentHelper管理Fragment
        mHomeFragmentHelper = new MainFragmentHelper();
        // 默认选择 拾意
        mHomeFragmentHelper.replaceFragment(new ShiyiFragment(), this);
        navigationView.setCheckedItem(R.id.nav_shiyi);
    }

    @Override
    public void onBackPressed() {
        // 重写返回键，优先关闭菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mHomeFragmentHelper.onCreateOptionsMenu(this, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mHomeFragmentHelper.onOptionsItemSelected(id)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 切换Fragment
        mHomeFragmentHelper.onNavigationItemSelected(item, this);
        // 关闭菜单
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected MainContract.MainPresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle fragmentBundle = new Bundle();
        mHomeFragmentHelper.getCurFragment().onSaveInstanceState(fragmentBundle, null);
        outState.putBundle("bundle_fragment", fragmentBundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Bundle fragmentBundle = savedInstanceState.getBundle("bundle_fragment");
            if (fragmentBundle != null) {
                mHomeFragmentHelper.getCurFragment().onRestoreInstanceState(fragmentBundle);
            }
        }
    }
}
