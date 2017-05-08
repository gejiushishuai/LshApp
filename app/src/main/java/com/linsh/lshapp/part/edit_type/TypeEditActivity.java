package com.linsh.lshapp.part.edit_type;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.TypeLabel;
import com.linsh.lshutils.tools.LshItemDragHelper;

import java.util.List;

import butterknife.BindView;

public class TypeEditActivity extends BaseToolbarActivity<TypeEditContract.Presenter> implements TypeEditContract.View {

    @BindView(R.id.rcv_type_edit_content)
    RecyclerView rcvContent;
    private TypeEditAdapter mAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_type_edit;
    }

    @Override
    protected String getToolbarTitle() {
        return "管理类型";
    }

    @Override
    protected TypeEditContract.Presenter initPresenter() {
        return new TypeEditPresenter();
    }

    @Override
    protected void initView() {
        rcvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new TypeEditAdapter();
        rcvContent.setAdapter(mAdapter);

        LshItemDragHelper itemDragHelper = new LshItemDragHelper(mAdapter);
        itemDragHelper.attachToRecyclerView(rcvContent);
    }

    @Override
    public void setData(List<TypeLabel> typeLabels) {
        mAdapter.setData(typeLabels);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_type_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_type_edit_confirm) {
            mPresenter.saveTypes(mAdapter.getData());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
