package com.linsh.lshapp.mvp.home.yingmao;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseMainFragment;
import com.linsh.lshapp.model.bean.SignIn;
import com.linsh.lshapp.view.LshPopupWindow;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;

import java.util.List;

/**
 * Created by linsh on 17/1/25.
 */

public class YingmaoFragment extends BaseMainFragment<YingmaoContract.Presenter> implements YingmaoContract.View {


    private RecyclerView mRcv;
    private YingmaoAdapter mAdapter;

    @Override
    protected String getTitle() {
        return "应卯";
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_yingmao;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void initView(View view) {
        mRcv = (RecyclerView) view.findViewById(R.id.rcv_yingmao_content);
        mRcv.setLayoutManager(new LinearLayoutManager(LshApplicationUtils.getContext()));
        mAdapter = new YingmaoAdapter();
        mRcv.setAdapter(mAdapter);
        mAdapter.setAdapterListener(new YingmaoAdapter.AdapterListener() {
            @Override
            public void onSignInClick(int position) {
                mPresenter.signIn(mAdapter.getData().get(position));
            }
        });
        mAdapter.setOnItemLongClickListener((view1, position) -> {
            SignIn signIn = mAdapter.getData().get(position);
            String index0 = signIn.getState() == SignIn.STATE_IGNORED ? "取消忽略" : "忽略";
            String[] items = {index0};
            new LshPopupWindow(getActivity())
                    .BuildList()
                    .setItems(items, (window, index) -> {
                        window.dismiss();
                        switch (index) {
                            case 0:
                                mPresenter.switchIgnore(signIn);
                                mAdapter.notifyDataSetChanged();
                                break;
                        }
                    })
                    .showAtLocation(mRcv, Gravity.CENTER, 0, 0);
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    public int getOptionsMenuItems() {
        return R.menu.fragment_yingmao;
    }

    @Override
    public boolean onOptionsItemSelected(int id) {
        switch (id) {
            case R.id.menu_fragment_yingmao_sign_in:
                mPresenter.signInAll();
                break;
        }
        return true;
    }

    @Override
    protected YingmaoContract.Presenter initPresenter() {
        return new YingmaoPresenter();
    }

    @Override
    public void setData(List<SignIn> signIns) {
        mAdapter.setData(signIns);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.refreshData();
    }
}
