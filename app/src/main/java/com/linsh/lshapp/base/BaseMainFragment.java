package com.linsh.lshapp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.linsh.lshapp.part.home.MainActivity;

/**
 * Created by linsh on 17/1/30.
 */

public abstract class BaseMainFragment extends BaseFragment<MainActivity> implements BaseView {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity myActivity = getMyActivity();
        myActivity.setTitle(getTitle());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract String getTitle();

    @Override
    protected abstract int getLayout();

    @Override
    protected abstract void initView(View view);

    @Override
    protected abstract void initData();

    public abstract int getOptionsMenuItems();

    public abstract boolean onOptionsItemSelected(int id);

}
