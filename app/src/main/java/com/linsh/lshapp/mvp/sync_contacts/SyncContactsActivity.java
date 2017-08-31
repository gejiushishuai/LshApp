package com.linsh.lshapp.mvp.sync_contacts;

import android.Manifest;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshutils.utils.LshPermissionUtils;

import java.util.List;

public class SyncContactsActivity extends BaseToolbarActivity<SyncContactsContract.Presenter>
        implements SyncContactsContract.View, SyncContactsAdapter.OnImportContactsListener, LshPermissionUtils.PermissionListener {

    private RecyclerView mRcvContent;
    private SyncContactsAdapter mAdapter;
    private int curItem = -1;

    @Override
    protected String getToolbarTitle() {
        return "同步手机联系人";
    }

    @Override
    protected SyncContactsContract.Presenter initPresenter() {
        return new SyncContactsPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_import_contacts;
    }

    @Override
    protected void initView() {
        mRcvContent = (RecyclerView) findViewById(R.id.rcv_import_contact);
        mAdapter = new SyncContactsAdapter();
        mRcvContent.setLayoutManager(new LinearLayoutManager(this));
        mRcvContent.setAdapter(mAdapter);
        mAdapter.setOnImportContactsListener(this);

        if (!LshPermissionUtils.checkPermission(Manifest.permission.WRITE_CONTACTS)) {
            LshPermissionUtils.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, this);
        }
    }

    @Override
    public void setData(List<ContactMixer> contactMixers) {
        mAdapter.setData(contactMixers);
    }

    @Override
    public void updateItem() {
        if (curItem != -1) {
            mAdapter.notifyItemChanged(curItem);
        }
    }

    @Override
    public void onClickStatus(ContactMixer mixer, int position) {
        if (LshPermissionUtils.checkPermission(Manifest.permission.WRITE_CONTACTS)) {
            curItem = position;
            mPresenter.onClickStatus(mixer);
        } else {
            LshPermissionUtils.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, this);
        }
    }

    @Override
    public void onGranted(String permission) {

    }

    @Override
    public void onDenied(String permission, boolean isNeverAsked) {

    }

    @Override
    public void onBeforeAndroidM(String permission) {

    }
}
