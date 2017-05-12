package com.linsh.lshapp.part.setting;

import android.os.Environment;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.common.LshConfig;
import com.linsh.lshutils.utils.Basic.LshToastUtils;
import com.linsh.lshutils.utils.LshFragmentUtils;
import com.linsh.lshutils.view.LshColorDialog;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;

/**
 * Created by Senh Linsh on 17/5/2.
 */
public class SettingsActivity extends BaseToolbarActivity<SettingsContract.Presenter> implements SettingsContract.View {


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
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk";
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            LshToastUtils.showToast("正在升级...");
            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), path);
        } else {
            LshToastUtils.showToast("没有可以升级的补丁");
        }
    }
}
