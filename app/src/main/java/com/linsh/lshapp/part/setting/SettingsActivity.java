package com.linsh.lshapp.part.setting;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshutils.utils.LshFragmentUtils;

/**
 * Created by Senh Linsh on 17/5/2.
 */
public class SettingsActivity extends BaseToolbarActivity<SettingsPresenter> implements SettingsContract.View {


    @Override
    protected String getToolbarTitle() {
        return "设置";
    }

    @Override
    protected SettingsPresenter initPresenter() {
        return new SettingsPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView() {
        LshFragmentUtils.replaceFragment(new SettingsFragment(), R.id.fl_settings_content, getActivity());
    }

    public void outputDatabase() {
        mPresenter.outputDatabase();
    }
}
