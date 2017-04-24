package com.linsh.lshapp.base;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Senh Linsh on 16/12/5.
 */
public abstract class BaseFragment<T extends BaseActivity> extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layout = getLayout();
        if (layout == 0) {
            return new FrameLayout(getActivity());
        }
        return View.inflate(getActivity(), layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    protected abstract int getLayout();

    protected abstract void initView(View view);

    protected abstract void initData();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public T getMyActivity() {
        try {
            return (T) getActivity();
        } catch (Exception e) {
            return null;
        }
    }
}
