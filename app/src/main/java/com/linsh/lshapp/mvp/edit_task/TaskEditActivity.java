package com.linsh.lshapp.mvp.edit_task;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.linsh.dialog.LshColorDialog;
import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.db.huhu.Frequency;
import com.linsh.lshapp.model.bean.db.huhu.Task;
import com.linsh.lshapp.view.EditTextPreference;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.utilseverywhere.ToastUtils;
import com.linsh.utilseverywhere.tools.IntentBuilder;

import butterknife.BindView;
import butterknife.OnClick;

public class TaskEditActivity extends BaseToolbarActivity<TaskEditContract.Presenter> implements TaskEditContract.View {

    @BindView(R.id.tp_task_edit_name)
    EditTextPreference tpTaskName;
    @BindView(R.id.tp_task_edit_frequency)
    EditTextPreference tpFrequency;

    private long mTaskId = -1;
    private MenuItem mConfirmItem;
    private String emptyText = "未填写";

    @Override
    protected String getToolbarTitle() {
        long personId = getHuhuTaskId();
        return personId > 0 ? "编辑任务" : "添加任务";
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_task_edit;
    }

    @Override
    protected void initView() {
    }

    @OnClick({R.id.tp_task_edit_name, R.id.tp_task_edit_frequency})
    public void clickItems(View view) {
        switch (view.getId()) {
            case R.id.tp_task_edit_name:
                editName();
                break;
            case R.id.tp_task_edit_frequency:
                editFrequency();
                break;
        }
    }

    // 点击修改任务名称
    private void editName() {
        final String lastName = getName();
        new LshColorDialog(getActivity())
                .buildInput()
                .setTitle("任务名称")
                .setText(emptyToEmpty(lastName))
                .setPositiveButton(null, new LshColorDialog.OnInputPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog, String inputText) {
                        if (isEmpty(inputText)) {
                            ToastUtils.show("赐个名称吧, 少爷");
                            return;
                        }
                        if (!inputText.equals(lastName)) {
                            setName(inputText);
                            onPersonModified();
                            if (getHuhuTaskId() == 0) {
                                mPresenter.checkName(inputText);
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(null, null)
                .show();
    }

    // 点击修改频率
    private void editFrequency() {
        final String lastFrequency = getFrequency();
        new LshColorDialog(getActivity())
                .buildInput()
                .setTitle("频率")
                .setText(emptyToEmpty(lastFrequency))
                .setPositiveButton(null, new LshColorDialog.OnInputPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog, String inputText) {
                        if (isEmpty(inputText)) {
                            ToastUtils.show("不填点什么?");
                            return;
                        }
                        Frequency frequency = Frequency.parse(inputText);
                        if (frequency != null) {
                            String frequencyStr = frequency.getFrequency();
                            if (!inputText.equals(lastFrequency)) {
                                setFrequency(frequencyStr);
                                onPersonModified();
                            }
                        } else {
                            ToastUtils.show("格式不正确");
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(null, null)
                .show();
    }

    // 个人信息被修改
    public void onPersonModified() {
        // 只有名字和组别填写之后才设置确认修改按钮为可用
        if (mConfirmItem == null) {
            mConfirmItem = getToolbar().getMenu().findItem(R.id.menu_shiyi_edit_task_confirm);
        }
        if (mConfirmItem == null) {
            showToast("Maybe there is something wrong with confirm menu item, please note!");
        } else if (!mConfirmItem.isEnabled() && !isEmpty(getName()) && !isEmpty(getFrequency())) {
            mConfirmItem.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_task_edit, menu);
        mConfirmItem = menu.findItem(R.id.menu_shiyi_edit_task_confirm);
        mConfirmItem.setEnabled(false); // 默认开始时确认修改按钮不可用
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_shiyi_edit_task_confirm) {
            saveTask();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTask() {
        mPresenter.saveTask(getHuhuTaskId(), getName(), getFrequency());
    }

    @Override
    protected TaskEditContract.Presenter initPresenter() {
        return new TaskEditPresent();
    }

    public String getName() {
        return emptyToEmpty(tpTaskName.detail().getText().toString());
    }

    public void setName(String name) {
        tpTaskName.detail().setText(emptyToEmptyText(name));
    }

    public String getFrequency() {
        return emptyToEmpty(tpFrequency.detail().getText().toString());
    }

    public void setFrequency(String frequency) {
        tpFrequency.detail().setText(emptyToEmptyText(frequency));
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

    @Override
    public long getHuhuTaskId() {
        if (mTaskId < 0) {
            mTaskId = IntentBuilder.getLongExtra(getActivity());
        }
        return mTaskId;
    }

    @Override
    public void setData(Task task) {
        setName(task.getTitle());
        setFrequency(task.getFrequency());
    }
}
