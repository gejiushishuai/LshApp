package com.linsh.lshapp.mvp.type_detail;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.TypeDetail;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.view.LshColorDialog;

import butterknife.BindView;

public class TypeDetailActivity extends BaseToolbarActivity<TypeDetailContract.Presenter> implements TypeDetailContract.View {

    @BindView(R.id.et_type_detail_info)
    EditText etInfo;
    @BindView(R.id.et_type_detail_desc)
    EditText etDesc;
    @BindView(R.id.tv_type_detail_timestamp)
    TextView tvTimestamp;

    private boolean isEditMode;

    @Override
    protected int getLayout() {
        return R.layout.activity_type_detail;
    }

    @Override
    protected void initView() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mPresenter.getTypeDetail() == null) {
            return false;
        }

        getMenuInflater().inflate(R.menu.activity_type_detail, menu);
        menu.getItem(0).setVisible(!isEditMode);
        menu.getItem(1).setVisible(!isEditMode);
        menu.getItem(2).setVisible(isEditMode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_shiyi_type_detail_edit:
                setEditMode();
                invalidateOptionsMenu();
                break;
            case R.id.menu_shiyi_type_detail_confirm:
                if (LshStringUtils.isEmpty(etInfo.getText().toString())) {
                    showToast("类型信息不能为空");
                } else {
                    mPresenter.saveTypeDetail(etInfo.getText().toString().trim(), etDesc.getText().toString().trim());
                }
                break;
            case R.id.menu_shiyi_type_detail_delete:
                showTextDialog("确定删除该条类型信息?", "删除", new LshColorDialog.OnPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog) {
                        dialog.dismiss();
                        mPresenter.deleteTypeDetail();
                    }
                }, null, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected TypeDetailContract.Presenter initPresenter() {
        return new TypeDetailPresenter();
    }

    @Override
    protected String getToolbarTitle() {
        return LshActivityUtils.getStringExtra(getActivity(), 0);
    }

    @Override
    public String getTypeDetailId() {
        return LshActivityUtils.getStringExtra(getActivity(), 1);
    }

    @Override
    public void setData(TypeDetail typeDetail) {
        tvTimestamp.setText("更新时间: " + typeDetail.getStringTimestamp());

        String detail = typeDetail.getDetail();
        String describe = typeDetail.getDescribe();
        if (LshStringUtils.isAllEmpty(detail, describe)) {
            setEditMode();
        } else {
            etInfo.setText(LshStringUtils.nullStrToEmpty(detail));
            etDesc.setText(LshStringUtils.nullStrToEmpty(describe));
            etInfo.clearFocus();
            etDesc.clearFocus();
        }
        invalidateOptionsMenu();
    }

    private void setEditMode() {
        isEditMode = true;
        etInfo.setEnabled(true);
        etDesc.setEnabled(true);
        etInfo.requestFocus();
        etInfo.setSelection(etInfo.getText().length());
    }
}
