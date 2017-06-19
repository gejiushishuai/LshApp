package com.linsh.lshapp.mvp.import_contacts;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.tamir7.contacts.Contact;
import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;

import java.util.List;

public class ImportContactsActivity extends BaseToolbarActivity<ImportContactsContract.Presenter>
        implements ImportContactsContract.View, ImportContactsAdapter.OnImportContactsListener {

    private RecyclerView mRcvContent;
    private ImportContactsAdapter mAdapter;
    private int curItem = -1;

    @Override
    protected String getToolbarTitle() {
        return "导入联系人";
    }

    @Override
    protected ImportContactsContract.Presenter initPresenter() {
        return new ImportContactsPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_import_contacts;
    }

    @Override
    protected void initView() {
        mRcvContent = (RecyclerView) findViewById(R.id.rcv_import_contact);
        mAdapter = new ImportContactsAdapter();
        mRcvContent.setLayoutManager(new LinearLayoutManager(this));
        mRcvContent.setAdapter(mAdapter);
        mAdapter.setOnImportContactsListener(this);
    }

    @Override
    public void setData(List<Contact> contacts) {
        mAdapter.setData(contacts);
    }

    @Override
    public void removeCurrentItem() {
        if (curItem != -1) {
            mAdapter.getData().remove(curItem);
            mAdapter.notifyItemRemoved(curItem);
        }
    }

    @Override
    public void onAddContact(Contact contact, int position) {
        mPresenter.addContact(contact);
        curItem = position;
    }
}
