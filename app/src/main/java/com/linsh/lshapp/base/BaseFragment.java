package com.linsh.lshapp.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;


/**
 * Created by Senh Linsh on 16/12/5.
 */
public abstract class BaseFragment<T extends BaseActivity> extends Fragment implements BaseView {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

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
