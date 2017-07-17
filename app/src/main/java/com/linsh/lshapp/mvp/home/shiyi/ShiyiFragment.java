package com.linsh.lshapp.mvp.home.shiyi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseMainFragment;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.mvp.edit_person.PersonEditActivity;
import com.linsh.lshapp.mvp.edit_type.TypeEditActivity;
import com.linsh.lshapp.mvp.person_detail.PersonDetailActivity;
import com.linsh.lshapp.view.LshPopupWindow;
import com.linsh.lshutils.adapter.LshExpandableRcvAdapter;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.view.LshColorDialog;

import java.util.List;

/**
 * Created by linsh on 17/1/25.
 */

public class ShiyiFragment extends BaseMainFragment<ShiyiContract.Presenter> implements ShiyiContract.View {

    private ShiyiAdapter mShiyiAdapter;
    private RecyclerView mRcv;
    private int mCurRestoreItem;
    private int mCurRestoreExpandedPosition = -1;

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
        mRcv = (RecyclerView) view.findViewById(R.id.rcv_shiyi_content);
        mRcv.setLayoutManager(new LinearLayoutManager(LshApplicationUtils.getContext()));
        mShiyiAdapter = new ShiyiAdapter();
        mRcv.setAdapter(mShiyiAdapter);

        mShiyiAdapter.setOnItemLongClickListener(new LshExpandableRcvAdapter.OnItemLongClickListener() {
            @Override
            public boolean onFirstLevelItemLongClick(View view, int firstLevelPosition) {
                showGroupLongClickSetting(view, firstLevelPosition);
                return true;
            }

            @Override
            public boolean onSecondLevelItemLongClick(View view, int secondLevelPosition) {
                return false;
            }
        });
        mShiyiAdapter.setOnItemClickListener(new LshExpandableRcvAdapter.OnItemClickListener<Group, Person>() {
            @Override
            public void onFirstLevelItemClick(Group firstLevelData, int firstLevelPosition, boolean expand) {

            }

            @Override
            public void onSecondLevelItemClick(Person SecondLevelData, int firstLevelPosition, int secondLevelPosition) {
                LshActivityUtils.newIntent(PersonDetailActivity.class)
                        .putExtra(SecondLevelData.getId())
                        .startActivityForResult(getActivity(), 101);
            }
        });
    }

    // 长按分组, 显示设置弹窗
    private void showGroupLongClickSetting(View view, final int position) {
        LshPopupWindow popupWindow = new LshPopupWindow(getActivity());
        popupWindow.BuildList()
                .setItems(new String[]{"添加分组", "重命名分组", "删除分组"}, new LshPopupWindow.OnItemClickListener() {
                    @Override
                    public void onClick(LshPopupWindow window, int index) {
                        switch (index) {
                            // 添加分组
                            case 0:
                                showAddGroupDialog();
                                break;
                            // 重命名分组
                            case 1:
                                showRenameGroupDialog(position);
                                break;
                            // 删除分组
                            case 2:
                                mPresenter.deleteGroup(position);
                                break;
                        }
                        window.dismiss();
                    }
                })
                .showAsDropDown(view, view.getWidth() / 2 - popupWindow.getWidth() / 2, 0);
    }

    private void startManageGroupActivity() {
        LshActivityUtils.newIntent(TypeEditActivity.class)
                .putExtra(TypeEditActivity.MANAGER_GROUPS)
                .startActivity(getActivity());
    }

    private void showAddGroupDialog() {
        new LshColorDialog(getMyActivity())
                .buildInput()
                .setTitle("添加分组")
                .setNegativeButton(null, null)
                .setPositiveButton("添加", new LshColorDialog.OnInputPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog, String inputText) {
                        String newGroupName = inputText.trim();
                        if (!LshStringUtils.isTrimEmpty(newGroupName)) {
                            dialog.dismiss();
                            mPresenter.addGroup(newGroupName);
                        }
                    }
                })
                .show();
    }

    private void showRenameGroupDialog(final int position) {
        new LshColorDialog(getMyActivity())
                .buildInput()
                .setTitle("重命名分组")
                .setText(mShiyiAdapter.getData().get(position).getName())
                .setNegativeButton(null, null)
                .setPositiveButton("重命名", new LshColorDialog.OnInputPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog, String inputText) {
                        String newGroupName = inputText.trim();
                        if (!LshStringUtils.isTrimEmpty(newGroupName)) {
                            dialog.dismiss();
                            mPresenter.renameGroup(position, newGroupName);
                        }
                    }
                })
                .show();
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
        switch (id) {
            case R.id.menu_fragment_shiyi_add_person:
                startActivityForResult(new Intent(getActivity(), PersonEditActivity.class), 100);
                break;
            case R.id.menu_fragment_shiyi_add_group:
                showAddGroupDialog();
                break;
            case R.id.menu_fragment_shiyi_manage_group:
                startManageGroupActivity();
                break;
        }
        return true;
    }

    @Override
    public void setData(List<Group> groups) {
        if (mCurRestoreExpandedPosition >= 0) {
            mShiyiAdapter.setData(groups, mCurRestoreExpandedPosition);
            mCurRestoreExpandedPosition = -1;
            mRcv.scrollToPosition(mCurRestoreItem);
        } else {
            mShiyiAdapter.setData(groups);
        }
    }

    @Override
    protected ShiyiContract.Presenter initPresenter() {
        return new ShiyiPresenter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, Void aVoid) {
        super.onSaveInstanceState(outState, aVoid);
        int curItem = ((LinearLayoutManager) mRcv.getLayoutManager()).findFirstVisibleItemPosition();
        int curExpandedPosition = mShiyiAdapter.getExpandedPosition();
        outState.putInt("curItem", curItem);
        outState.putInt("curExpandedPosition", curExpandedPosition);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mCurRestoreItem = savedInstanceState.getInt("curItem");
            mCurRestoreExpandedPosition = savedInstanceState.getInt("curExpandedPosition");
        }
    }
}
