package com.linsh.lshapp.mvp.home.huhu;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseMainFragment;
import com.linsh.lshapp.model.bean.db.huhu.Task;
import com.linsh.lshapp.mvp.edit_task.TaskEditActivity;
import com.linsh.lshutils.decoration.DividerItemDecoration;
import com.linsh.utilseverywhere.ContextUtils;
import com.linsh.utilseverywhere.IntentUtils;
import com.linsh.utilseverywhere.ResourceUtils;

import java.util.List;

/**
 * Created by linsh on 17/1/25.
 */

public class HuhuFragment extends BaseMainFragment<HuhuContract.Presenter> implements HuhuContract.View {


    private RecyclerView mRcv;
    private HuhuAdapter mAdapter;

    @Override
    protected String getTitle() {
        return "呼呼";
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_huhu;
    }

    @Override
    protected void initView(View view) {
        mRcv = view.findViewById(R.id.rcv_huhu_content);
        mRcv.setLayoutManager(new LinearLayoutManager(ContextUtils.get()));
        DividerItemDecoration decoration = new DividerItemDecoration(1, ResourceUtils.getColor(R.color.line_whitebg));
        decoration.setShowDividers(DividerItemDecoration.SHOW_DIVIDER_MIDDLE);
        mRcv.addItemDecoration(decoration);
        mAdapter = new HuhuAdapter();
        mAdapter.setAdapterListener(new HuhuAdapter.AdapterListener() {
            @Override
            public void onSignInClick(int position) {
                mPresenter.finishTask(mAdapter.getData().get(position).getId());
            }

            @Override
            public void onStopClick(int position) {
            }

            @Override
            public void onDeleteClick(int position) {
                showTextDialog("我只是确认一下你是不是一不小心的...", "删除", dialog -> {
                    dialog.dismiss();
                    mPresenter.deleteTask(mAdapter.getData().get(position).getId());
                }, null, null);
            }
        });
        mRcv.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
    }

    @Override
    public int getOptionsMenuItems() {
        return R.menu.fragment_huhu;
    }

    @Override
    public boolean onOptionsItemSelected(int id) {
        switch (id) {
            case R.id.menu_fragment_huhu_add_task:
                IntentUtils.buildIntent(TaskEditActivity.class)
                        .startActivity(getActivity());
                break;
        }
        return true;
    }

    @Override
    protected HuhuContract.Presenter initPresenter() {
        return new HuhuPresenter();
    }

    @Override
    public void setData(List<Task> tasks) {
        mAdapter.setData(tasks);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.updateAllTasks();
    }
}
