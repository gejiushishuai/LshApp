package com.linsh.lshapp.mvp.type_detail;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.linsh.dialog.LshColorDialog;
import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.db.shiyi.TypeDetail;
import com.linsh.utilseverywhere.ClipboardUtils;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.utilseverywhere.tools.IntentBuilder;

public class TypeDetailActivity extends BaseToolbarActivity<TypeDetailContract.Presenter>
        implements TypeDetailContract.View, View.OnLongClickListener {

    EditText etInfo;
    EditText etDesc;
    TextView tvTimestamp;

    private boolean isEditMode;
    private TypeInfoHelper.TypeInfo mTypeInfo;

    @Override
    protected int getLayout() {
        return R.layout.activity_type_detail;
    }

    @Override
    protected void initView() {
        etInfo = findViewById(R.id.et_type_detail_info);
        etDesc = findViewById(R.id.et_type_detail_desc);
        tvTimestamp = findViewById(R.id.tv_type_detail_timestamp);


        mTypeInfo = TypeInfoHelper.getHelper(getToolbarTitle());
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
                if (StringUtils.isEmpty(etInfo.getText().toString())) {
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
        return IntentBuilder.getStringExtra(getActivity(), 0);
    }

    @Override
    public String getTypeDetailId() {
        return IntentBuilder.getStringExtra(getActivity(), 1);
    }

    @Override
    public void setData(TypeDetail typeDetail) {
        tvTimestamp.setText("更新时间: " + typeDetail.getStringTimestamp());

        String detail = typeDetail.getDetail();
        String describe = typeDetail.getDescribe();

        if (StringUtils.isAllEmpty(detail, describe)) {
            setEditMode();
        } else {
            etInfo.setText(StringUtils.nullStrToEmpty(detail));
            etInfo.setOnLongClickListener(this);

            etDesc.setText(StringUtils.nullStrToEmpty(describe));
            etDesc.clearFocus();
            etDesc.setOnLongClickListener(this);
            etDesc.setFocusable(false);
            etDesc.setFocusableInTouchMode(false);

            mTypeInfo.setDisplayMode(etInfo);
        }
        invalidateOptionsMenu();
    }

    private void setEditMode() {

        isEditMode = true;
        etDesc.setOnLongClickListener(null);
        etDesc.setFocusableInTouchMode(true);
        etDesc.setFocusable(true);

        mTypeInfo.setEditMode(etInfo);
    }

    @Override
    public boolean onLongClick(View v) {
        String text = ((TextView) v).getText().toString();
        if (!isEditMode && !StringUtils.isEmpty(text)) {
            ClipboardUtils.putText(text);
            showToast("文本已复制");
            return true;
        }
        return false;
    }
}
