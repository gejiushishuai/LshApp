package com.linsh.lshapp.tools;

import android.view.Menu;
import android.view.MenuItem;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseMainFragment;
import com.linsh.lshapp.mvp.home.MainActivity;
import com.linsh.lshapp.mvp.home.huhu.HuhuFragment;
import com.linsh.lshapp.mvp.home.miqi.MiqiFragment;
import com.linsh.lshapp.mvp.home.shiyi.ShiyiFragment;
import com.linsh.lshapp.mvp.home.yingmao.YingmaoFragment;
import com.linsh.lshapp.mvp.search.SearchActivity;
import com.linsh.lshapp.mvp.setting.SettingsActivity;
import com.linsh.lshapp.utils.LshFragmentUtils;
import com.linsh.utilseverywhere.IntentUtils;

/**
 * Created by linsh on 17/1/25.
 */

public class MainFragmentHelper {

    private BaseMainFragment mFragment;

    // 菜单栏被选择时
    public boolean onNavigationItemSelected(MenuItem item, MainActivity activity) {
        int id = item.getItemId();
        BaseMainFragment fragment = null;
        if (id == R.id.nav_shiyi) {
            fragment = new ShiyiFragment();
        } else if (id == R.id.nav_miqi) {
            fragment = new MiqiFragment();
        } else if (id == R.id.nav_shencai) {
//            fragment = new ShenCaiFragment();
        } else if (id == R.id.nav_huhu) {
            fragment = new HuhuFragment();
        } else if (id == R.id.nav_yingmao) {
            fragment = new YingmaoFragment();
        } else if (id == R.id.nav_seek) {
            IntentUtils.buildIntent(SearchActivity.class).startActivity(activity);
        } else if (id == R.id.nav_setting) {
            IntentUtils.buildIntent(SettingsActivity.class).startActivity(activity);
        }
        if (fragment != null) {
            replaceFragment(fragment, activity);
        }
        return true;
    }

    public BaseMainFragment getCurFragment() {
        return mFragment;
    }

    public void replaceFragment(BaseMainFragment fragment, MainActivity activity) {
        mFragment = fragment;
        LshFragmentUtils.replaceFragment(fragment, R.id.fl_home_content, activity);
        // 刷新Menu菜单
        activity.invalidateOptionsMenu();
    }

    // 点击选择选项菜单
    public boolean onOptionsItemSelected(int id) {
        return mFragment.onOptionsItemSelected(id);
    }

    // 点击Menu菜单时调用此方法创建选项菜单
    public void onCreateOptionsMenu(MainActivity activity, Menu menu) {
        int menuRes = mFragment.getOptionsMenuItems();
        if (mFragment != null && menuRes > 0) {
            activity.getMenuInflater().inflate(menuRes, menu);
        }
    }
}
