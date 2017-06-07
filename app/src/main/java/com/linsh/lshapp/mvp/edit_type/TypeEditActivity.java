package com.linsh.lshapp.mvp.edit_type;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.Typable;
import com.linsh.lshutils.tools.LshItemDragHelper;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.view.LshColorDialog;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class TypeEditActivity extends BaseToolbarActivity<TypeEditContract.Presenter> implements TypeEditContract.View, LshItemDragHelper.IItemDragCallback {

    @BindView(R.id.rcv_type_edit_content)
    RecyclerView rcvContent;

    public static final int MANAGER_TYPE_LABELS = 0;
    public static final int MANAGER_PERSON_TYPES = 1;
    public static final int MANAGER_GROUPS = 2;
    private TypeEditAdapter mAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_type_edit;
    }

    @Override
    protected String getToolbarTitle() {
        int intExtra = LshActivityUtils.getIntExtra(getActivity());
        if (intExtra == MANAGER_PERSON_TYPES) {
            return "管理联系人类型";
        } else if (intExtra == MANAGER_GROUPS) {
            return "管理分组";
        } else {
            return "管理类型";
        }
    }

    @Override
    protected TypeEditContract.Presenter initPresenter() {
        int intExtra = LshActivityUtils.getIntExtra(getActivity());
        if (intExtra == MANAGER_PERSON_TYPES) {
            return new EditPersonTypePresenter();
        } else if (intExtra == MANAGER_GROUPS) {
            return new EditGroupPresenter();
        } else {
            return new EditTypeLabelPresenter();
        }
    }

    @Override
    protected void initView() {
        rcvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new TypeEditAdapter();
        rcvContent.setAdapter(mAdapter);

        LshItemDragHelper itemDragHelper = new LshItemDragHelper(this);
        itemDragHelper.attachToRecyclerView(rcvContent);
    }

    @Override
    public <T extends Typable> void setData(List<T> typeLabels) {
        mAdapter.setData((List<Typable>) typeLabels);
    }

    @Override
    public String getPersonId() {
        return LshActivityUtils.getStringExtra(getActivity());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_type_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_type_edit_confirm) {
            item.setEnabled(false);
            mPresenter.saveTypes(mAdapter.getData());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        if (mPresenter instanceof EditGroupPresenter) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, int fromPosition, int toPosition) {
        Collections.swap(mAdapter.getData(), fromPosition, toPosition);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(final int position, int direction) {
        showTextDialog("是否要删除该条类型?", "删除", new LshColorDialog.OnPositiveListener() {
            @Override
            public void onClick(LshColorDialog dialog) {
                dialog.dismiss();
                mPresenter.removeType(mAdapter.getData().get(position).getName(), position);
            }
        }, null, new LshColorDialog.OnNegativeListener() {
            @Override
            public void onClick(LshColorDialog dialog) {
                dialog.dismiss();
                deletedTypeFromRealm(false, position);
            }
        });
    }

    @Override
    public void deletedTypeFromRealm(boolean isSuccess, int position) {
        if (isSuccess) {
            // Realm删除数据后, Copy的数据也会因此更新? 我猜是的!
            mAdapter.notifyItemRemoved(position);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMoved(RecyclerView recyclerView, int fromPosition, int toPosition) {

    }
}
