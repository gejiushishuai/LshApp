package com.linsh.lshapp.part.setting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.linsh.lshapp.R;

/**
 * Created by Senh Linsh on 17/5/2.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        findPreference("output_database").setOnPreferenceClickListener(this);
        findPreference("import_json").setOnPreferenceClickListener(this);
        findPreference("check_update").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "output_database":
                ((SettingsActivity)getActivity()).outputDatabase();
                break;
            case "import_json":
                ((SettingsActivity)getActivity()).importGson();
                break;
            case "check_update":
                ((SettingsActivity)getActivity()).checkUpdate();
                break;
        }
        return true;
    }
}
