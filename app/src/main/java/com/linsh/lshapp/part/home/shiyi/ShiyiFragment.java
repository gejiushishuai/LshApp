package com.linsh.lshapp.part.home.shiyi;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseMainFragment;
import com.linsh.lshapp.model.Group;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;

import io.realm.RealmList;

/**
 * Created by linsh on 17/1/25.
 */

public class ShiyiFragment extends BaseMainFragment<ShiyiContract.Presenter> implements ShiyiContract.View {

    private ShiyiAdapter mShiyiAdapter;

    @Override
    protected String getTitle() {
        return "拾意";
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_shiyi;
    }

    @Override
    protected void initView(View view) {
        RecyclerView mRcv = (RecyclerView) view.findViewById(R.id.rcv_shiyi_content);
        mRcv.setLayoutManager(new LinearLayoutManager(LshApplicationUtils.getContext()));
        mShiyiAdapter = new ShiyiAdapter();
        mRcv.setAdapter(mShiyiAdapter);
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
        return true;
    }


    @Override
    public void setData(RealmList<Group> groups) {
        mShiyiAdapter.setData(groups);
    }

    @Override
    protected ShiyiContract.Presenter initPresenter() {
        return new ShiyiPresenter();
    }
}
