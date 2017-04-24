package com.linsh.lshapp.part.home.shiyi;

import android.view.View;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseMainFragment;

/**
 * Created by linsh on 17/1/25.
 */

public class ShiyiFragment extends BaseMainFragment {

    @Override
    protected String getTitle() {
        return "拾意";
    }

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initData() {

    }

    @Override
    public int getOptionsMenuItems() {
        return R.menu.fragment_shiyi;
    }

    @Override
    public boolean onOptionsItemSelected(int id) {
        return false;
    }
}
