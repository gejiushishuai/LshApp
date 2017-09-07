package com.linsh.lshapp.mvp.sync_contacts;

import android.Manifest;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.ContactsPerson;
import com.linsh.lshapp.model.bean.ShiyiContact;
import com.linsh.lshapp.mvp.person_detail.PersonDetailActivity;
import com.linsh.lshapp.view.LshPopupWindow;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.utils.LshIntentUtils;
import com.linsh.lshutils.utils.LshPermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class SyncContactsActivity extends BaseToolbarActivity<SyncContactsContract.Presenter>
        implements SyncContactsContract.View, SyncContactsAdapter.OnImportContactsListener, LshRecyclerViewAdapter.OnItemLongClickListener {

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
        mAdapter.setOnItemLongClickListener(this);

        if (!LshPermissionUtils.checkPermission(Manifest.permission.WRITE_CONTACTS)) {
            LshPermissionUtils.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, null);
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
            LshPermissionUtils.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, null);
        }
    }

    @Override
    public void onItemLongClick(int position) {
        if (!LshPermissionUtils.checkPermission(Manifest.permission.WRITE_CONTACTS)) {
            LshPermissionUtils.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, null);
            return;
        }
        curItem = position;

        View childAt = mRcvContent.getChildAt(position);
        ContactMixer mixer = mAdapter.getData().get(position);
        ShiyiContact contact = mixer.getContact();
        ContactsPerson person = mixer.getPerson();

        ArrayList<String> items = new ArrayList<>();
        if (contact != null) {
            items.add("跳转至该手机联系人");
        }
        if (person != null || mixer.getStatus() == ContactMixer.FINISH_UPDATE) {
            items.add("跳转至该拾意联系人");
        }
        if (mixer.getStatus() == ContactMixer.FINISH_UPDATE && contact != null && person != null &&
                LshStringUtils.isAllNotEmpty(contact.getPhotoUri(), person.getAvatar())) {
            items.add(0, "更新头像");
        }
        LshPopupWindow lshPopupWindow = new LshPopupWindow(this);
        lshPopupWindow.BuildList()
                .setItems(items, new LshPopupWindow.OnItemClickListener() {
                    @Override
                    public void onClick(LshPopupWindow window, int index) {
                        window.dismiss();
                        String item = items.get(index);
                        switch (item) {
                            case "更新头像":
                                mPresenter.refreshAvatar(mixer);
                                break;
                            case "跳转至该手机联系人":
                                gotoContact(contact);
                                break;
                            case "跳转至该拾意联系人":
                                String id = person != null ? person.getId() : contact.getPersonId();
                                LshActivityUtils.newIntent(PersonDetailActivity.class)
                                        .putExtra(id)
                                        .startActivity(getActivity());
                                break;
                        }
                    }
                })
                .showAsDropDown(childAt, childAt.getWidth() / 2 - lshPopupWindow.getWidth() / 2, 0);

    }

    private void gotoContact(ShiyiContact contact) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String lookupKey = contact.getLookupKey();
            if (LshStringUtils.notEmpty(lookupKey)) {
                LshIntentUtils.gotoContactDetail(contact.getId(), lookupKey);
            }
        } else {
            LshIntentUtils.gotoContacts();
        }
    }
}
