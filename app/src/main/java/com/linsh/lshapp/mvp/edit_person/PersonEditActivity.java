package com.linsh.lshapp.mvp.edit_person;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linsh.dialog.LshColorDialog;
import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.db.shiyi.Group;
import com.linsh.lshapp.model.bean.db.shiyi.Person;
import com.linsh.lshapp.mvp.person_detail.PersonDetailActivity;
import com.linsh.lshapp.tools.ImageTools;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshapp.tools.LshIdTools;
import com.linsh.utilseverywhere.IntentUtils;
import com.linsh.utilseverywhere.ListUtils;
import com.linsh.utilseverywhere.Rx.Action;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.utilseverywhere.ToastUtils;
import com.linsh.utilseverywhere.tools.IntentBuilder;

import java.io.File;
import java.util.List;

public class PersonEditActivity extends BaseToolbarActivity<PersonEditContract.Presenter> implements PersonEditContract.View, View.OnClickListener {

    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvDesc;
    private TextView tvSex;
    private TextView tvGroup;
    private ImageView ivSync;

    private MenuItem mConfirmItem;
    private String emptyText = "未填写";
    private static final int REQUEST_CODE_PICK_PHOTO = 100;
    private static final int REQUEST_CODE_CROP_PHOTO = 101;

    private File mCurPickedFile;
    private File mCurSelectedFile;
    private boolean idDataInitted;
    private boolean savedModified;

    @Override
    protected String getToolbarTitle() {
        String personId = getPersonId();
        return StringUtils.isEmpty(personId) ? "添加联系人" : "修改联系人";
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_person_add;
    }

    @Override
    protected void initView() {
        ivAvatar = findViewById(R.id.iv_shiyi_person_add_avatar);
        tvName = findViewById(R.id.tv_shiyi_person_add_name);
        tvDesc = findViewById(R.id.tv_shiyi_person_add_desc);
        tvSex = findViewById(R.id.tv_shiyi_person_add_sex);
        tvGroup = findViewById(R.id.tv_shiyi_person_add_group);
        ivSync = findViewById(R.id.iv_shiyi_person_add_sync);
        findViewById(R.id.rl_shiyi_person_add_avatar_item).setOnClickListener(this);
        findViewById(R.id.rl_shiyi_person_add_name_item).setOnClickListener(this);
        findViewById(R.id.rl_shiyi_person_add_desc_item).setOnClickListener(this);
        findViewById(R.id.rl_shiyi_person_add_sex_item).setOnClickListener(this);
        findViewById(R.id.rl_shiyi_person_add_group_item).setOnClickListener(this);
        findViewById(R.id.ll_shiyi_person_add_sync_item).setOnClickListener(this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        restoreInstanceState(savedInstanceState);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_shiyi_person_add_avatar_item:
                editAvatar();
                break;
            case R.id.rl_shiyi_person_add_name_item:
                addName();
                break;
            case R.id.rl_shiyi_person_add_desc_item:
                addDesc();
                break;
            case R.id.rl_shiyi_person_add_sex_item:
                addSex();
                break;
            case R.id.rl_shiyi_person_add_group_item:
                selectGroup();
                break;
            case R.id.ll_shiyi_person_add_sync_item:
                toggleSync();
                break;
            default:
                break;
        }
    }

    private void toggleSync() {
        ivSync.setSelected(!ivSync.isSelected());
        onPersonModified();
    }

    // 点击修改头像
    private void editAvatar() {
        IntentUtils.gotoPickPhoto(this, REQUEST_CODE_PICK_PHOTO);
    }

    // 点击修改姓名
    private void addName() {
        final String lastName = getName();
        new LshColorDialog(getActivity())
                .buildInput()
                .setTitle("填写姓名:")
                .setText(emptyToEmpty(lastName))
                .setPositiveButton(null, new LshColorDialog.OnInputPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog, String inputText) {
                        if (isEmpty(inputText)) {
                            ToastUtils.show("姓名不能为空");
                            return;
                        }
                        if (!inputText.equals(lastName)) {
                            setName(inputText);
                            onPersonModified();
                            mPresenter.checkName(inputText);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(null, null)
                .show();
    }

    // 点击修改描述
    private void addDesc() {
        final String lastDesc = getDesc();
        new LshColorDialog(getActivity())
                .buildInput()
                .setTitle("添加描述:")
                .setText(emptyToEmpty(lastDesc))
                .setPositiveButton(null, new LshColorDialog.OnInputPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog, String inputText) {
                        if (isEmpty(inputText)) {
                            ToastUtils.show("描述不能为空");
                            return;
                        }
                        if (!inputText.equals(lastDesc)) {
                            setDesc(inputText);
                            onPersonModified();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(null, null)
                .show();
    }

    // 点击修改性别
    private void addSex() {
        final String[] items = {"男", "女"};
        final String lastSex = getSex();
        new LshColorDialog(getActivity())
                .buildList()
                .setTitle("选择性别:")
                .setList(items)
                .setOnItemClickListener(new LshColorDialog.OnItemClickListener() {
                    @Override
                    public void onClick(LshColorDialog dialog, String item, int index) {
                        dialog.dismiss();
                        if (!lastSex.equals(items[index])) {
                            setSex(items[index]);
                            onPersonModified();
                        }
                    }
                })
                .show();
    }

    // 点击修改组别
    private void selectGroup() {
        final String lastGroup = getGroup();
        final List<String> groups = getGroups();
        new LshColorDialog(getActivity())
                .buildList()
                .setTitle("选择分组:")
                .setList(groups)
                .setOnItemClickListener(new LshColorDialog.OnItemClickListener() {
                    @Override
                    public void onClick(LshColorDialog dialog, String item, int index) {
                        dialog.dismiss();
                        // 点击第一条, 添加新分组
                        if (index == 0) {
                            new LshColorDialog(getActivity())
                                    .buildInput()
                                    .setTitle("添加新分组")
                                    .setPositiveButton("添加", new LshColorDialog.OnInputPositiveListener() {
                                        @Override
                                        public void onClick(LshColorDialog dialog, String inputText) {
                                            dialog.dismiss();
                                            if (!inputText.equals(lastGroup)) {
                                                mPresenter.addGroup(inputText);
                                            }
                                        }
                                    })
                                    .setNegativeButton(null, null)
                                    .show();
                        } else {
                            String group = groups.get(index);
                            if (!lastGroup.equals(group)) {
                                setGroup(group);
                                onPersonModified();
                            }
                        }
                    }
                })
                .show();
    }

    private List<String> getGroups() {
        List<String> groups = ListUtils.toStringList(mPresenter.getGroups(), new Action<String, Group>() {
            @Override
            public String call(Group group) {
                return group.getName();
            }
        });
        groups.add(0, "新建分组");
        return groups;
    }

    // 个人信息被修改
    public void onPersonModified() {
        // 只有名字和组别填写之后才设置确认修改按钮为可用
        if (mConfirmItem == null) {
            mConfirmItem = getToolbar().getMenu().findItem(R.id.menu_shiyi_add_person_confirm);
        }
        if (mConfirmItem == null) {
            showToast("Maybe there is something wrong with confirm menu item, please note!");
        } else if (!mConfirmItem.isEnabled() && !isEmpty(getName()) && !isEmpty(getGroup())) {
            mConfirmItem.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_person_add, menu);
        mConfirmItem = menu.findItem(R.id.menu_shiyi_add_person_confirm);
        mConfirmItem.setEnabled(savedModified); // 默认开始时确认修改按钮不可用
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_shiyi_add_person_confirm) {
            savePerson();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 选择照片后返回
            case REQUEST_CODE_PICK_PHOTO:
                if (resultCode == RESULT_OK && data != null) {
                    mCurPickedFile = LshFileFactory.getUploadAvatarFile(LshIdTools.getTimeId());
                    IntentUtils.gotoCropPhoto(this, REQUEST_CODE_CROP_PHOTO, data.getData(), Uri.fromFile(mCurPickedFile), 1, 1, 1600, 1600);
                }
                break;
            // 剪裁照片后返回
            case REQUEST_CODE_CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    ImageTools.setImage(ivAvatar, mCurPickedFile);
                    mCurSelectedFile = mCurPickedFile;
                    onPersonModified();
                }
                break;
        }
    }

    private void savePerson() {
        ////// 添加个人信息到选定的组里面, 并结束Activity
        mPresenter.savePerson(getGroup(), getName(), getDesc(), getSex(), getSync(), mCurSelectedFile);
    }

    @Override
    public void showPersonDetail(String personId) {
        finish();
        IntentUtils.buildIntent(PersonDetailActivity.class)
                .putExtra(personId)
                .startActivity(getActivity());
    }

    @Override
    public String getPersonId() {
        return IntentBuilder.getStringExtra(getActivity());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", tvName.getText().toString());
        outState.putString("desc", tvDesc.getText().toString());
        outState.putString("sex", tvSex.getText().toString());
        outState.putString("group", tvGroup.getText().toString());
        if (mCurSelectedFile != null) {
            outState.putString("avatar", mCurSelectedFile.getAbsolutePath());
        }
        if (mConfirmItem != null && mConfirmItem.isEnabled()) {
            outState.putBoolean("modified", true);
        }
    }

    protected void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setName(savedInstanceState.getString("name"));
            setDesc(savedInstanceState.getString("desc"));
            setSex(savedInstanceState.getString("sex"));
            setGroup(savedInstanceState.getString("group"));
            String avatarPath = savedInstanceState.getString("avatar");
            if (StringUtils.notEmpty(avatarPath)) {
                mCurSelectedFile = new File(avatarPath);
                ImageTools.setImage(ivAvatar, mCurSelectedFile);
            }
            savedModified = savedInstanceState.getBoolean("modified");
            idDataInitted = true;
        }
    }

    @Override
    public void setData(Person person) {
        if (!idDataInitted) {
            setAvatar(person.getAvatarThumb(), person.getAvatar());
            setName(person.getName());
            setDesc(person.getDescribe());
            setSex(person.getGender());
            setSync(person.isSyncWithContacts());
        }
        if (mCurSelectedFile == null) {
            setAvatar(person.getAvatarThumb(), person.getAvatar());
        }
    }

    @Override
    public void setData(String group) {
        if (!idDataInitted) {
            setGroup(group);
        }
    }

    @Override
    protected PersonEditContract.Presenter initPresenter() {
        return new PersonEditPresent();
    }

    private void setAvatar(String avatarThumb, String avatar) {
        if (!StringUtils.isEmpty(avatar)) {
            ImageTools.loadAvatar(ivAvatar, avatarThumb, avatar);
        }
    }

    public String getName() {
        return emptyToEmpty(tvName.getText().toString());
    }

    public void setName(String name) {
        tvName.setText(emptyToEmptyText(name));
    }

    public String getDesc() {
        return emptyToEmpty(tvDesc.getText().toString());
    }

    public void setDesc(String desc) {
        tvDesc.setText(emptyToEmptyText(desc));
    }

    public String getSex() {
        return emptyToEmpty(tvSex.getText().toString());
    }

    public void setSex(String sex) {
        tvSex.setText(emptyToEmptyText(sex));
    }

    public String getGroup() {
        return emptyToEmpty(tvGroup.getText().toString());
    }

    public void setGroup(String group) {
        tvGroup.setText(emptyToEmptyText(group));
    }

    public void setSync(boolean sync) {
        ivSync.setSelected(sync);
    }

    public boolean getSync() {
        return ivSync.isSelected();
    }

    private boolean isEmpty(String text) {
        return StringUtils.isEmpty(text) || emptyText.equals(text);
    }

    private String emptyToEmpty(String text) {
        return isEmpty(text) ? "" : text;
    }

    private String emptyToEmptyText(String text) {
        return isEmpty(text) ? emptyText : text;
    }
}
