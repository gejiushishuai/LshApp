package com.linsh.lshapp.mvp.setting;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.linsh.dialog.LshColorDialog;
import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.common.LshConfig;
import com.linsh.lshapp.mvp.sync_contacts.SyncContactsActivity;
import com.linsh.lshapp.service.ImportAppDataService;
import com.linsh.lshapp.utils.LshFragmentUtils;
import com.linsh.utilseverywhere.AccessibilityUtils;
import com.linsh.utilseverywhere.IntentUtils;
import com.linsh.utilseverywhere.PermissionUtils;

/**
 * Created by Senh Linsh on 17/5/2.
 */
public class SettingsActivity extends BaseToolbarActivity<SettingsContract.Presenter> implements SettingsContract.View, PermissionUtils.PermissionListener {


    @Override
    protected String getToolbarTitle() {
        return "设置";
    }

    @Override
    protected SettingsPresenter initPresenter() {
        return new SettingsPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView() {
        LshFragmentUtils.replaceFragment(new SettingsFragment(), R.id.fl_settings_content, getActivity());
    }

    public void outputDatabase() {
        mPresenter.outputDatabase();
    }

    public void outputWordRepo() {
        mPresenter.outputWordRepo();
    }

    public void importGson() {
        showTextDialog("请确保将Json文本数据按照表格名称存放在SD卡\"" + LshConfig.appNameEn + "/import/json\"目录",
                "是的", new LshColorDialog.OnPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog) {
                        dialog.dismiss();
                        mPresenter.importGson();
                    }
                }, null, null);
    }

    public void checkUpdate() {
        mPresenter.checkUpdate();
    }

    public void backupDatabase() {
        mPresenter.backupDatabase();
    }

    public void importContacts() {
        PermissionUtils.checkAndRequestPermission(this, Manifest.permission.READ_CONTACTS, this);
    }

    public void importWechatContacts() {
        if (checkOverlayPermission()) {
            if (AccessibilityUtils.checkAccessibility()) {
                startService(new Intent(this, ImportAppDataService.class)
                        .putExtra(ImportAppDataService.COMMAND, ImportAppDataService.COMMAND_OPEN));
            }
        }
    }

    private boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            PermissionUtils.requestPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW, null);
            return false;
        }
        return true;
    }

    @Override
    public void onGranted(String permission) {
        if (permission.equals(Manifest.permission.READ_CONTACTS)) {
            IntentUtils.buildIntent(SyncContactsActivity.class).startActivity(this);
        } else if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            importWechatContacts();
        }
    }

    @Override
    public void onDenied(String permission, boolean isNeverAsked) {
        if (permission.equals(Manifest.permission.READ_CONTACTS)) {
            showToast("您已拒绝了联系人权限, 请在权限界面允许该权限");
        } else if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            showToast("您已拒绝了显示悬浮窗权限, 请在权限界面允许该权限");
        }
        IntentUtils.gotoPermissionSetting();
    }

    @Override
    public void onBeforeAndroidM(String permission) {
        if (permission.equals(Manifest.permission.READ_CONTACTS)) {
            IntentUtils.buildIntent(SyncContactsActivity.class).startActivity(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }
}
