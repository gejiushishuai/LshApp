package com.linsh.lshapp.mvp.setting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.linsh.lshapp.R;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshAppUtils;

/**
 * Created by Senh Linsh on 17/5/2.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        findPreference("output_database").setOnPreferenceClickListener(this);
        findPreference("output_word_repository").setOnPreferenceClickListener(this);
        findPreference("import_json").setOnPreferenceClickListener(this);
        findPreference("import_contacts").setOnPreferenceClickListener(this);
        findPreference("backup_database").setOnPreferenceClickListener(this);
        Preference checkUpdate = findPreference("check_update");
        checkUpdate.setOnPreferenceClickListener(this);
        checkUpdate.setTitle(checkUpdate.getTitle() + LshStringUtils.format(" (当前版本: {版本号})", LshAppUtils.getVersionName()));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "output_database":
                ((SettingsActivity)getActivity()).outputDatabase();
                break;
            case "output_word_repository":
                ((SettingsActivity)getActivity()).outputWordRepo();
                break;
            case "import_json":
                ((SettingsActivity)getActivity()).importGson();
                break;
            case "import_contacts":
                ((SettingsActivity)getActivity()).importContacts();
                break;
            case "backup_database":
                ((SettingsActivity)getActivity()).backupDatabase();
                break;
            case "check_update":
                ((SettingsActivity)getActivity()).checkUpdate();
                break;
        }
        return true;
    }
}
