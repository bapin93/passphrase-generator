package com.example.andres.passphrasegenerator;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

    private Preference mCustomStrengthPreference;
    private Preference mBasicStrengthPreference;
    private String[] mStrengthValues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initialize();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;
        if (preference.getKey().equals("passphrase_strength")) {
            if (newValue.toString().equals(mStrengthValues[4])) {
                mCustomStrengthPreference.setEnabled(true);
                result = true;
            } else {
                mCustomStrengthPreference.setEnabled(false);
                result = true;
            }
        } else if (preference.getKey().equals("custom_strength")) {
            if (Integer.parseInt(newValue.toString()) < 3) {

            }
        }
        return result;
    }

    /**
     *
     */
    private void initialize() {
        mBasicStrengthPreference = findPreference("passphrase_strength");
        mCustomStrengthPreference = findPreference("custom_strength");
        mStrengthValues = getResources().getStringArray(R.array.strength_values);
        if(mBasicStrengthPreference.getSharedPreferences().getString(mBasicStrengthPreference
                .getKey(), mStrengthValues[1]).equals(mStrengthValues[4])) {
            mCustomStrengthPreference.setEnabled(true);
        }
        mBasicStrengthPreference.setOnPreferenceChangeListener(this);
        mBasicStrengthPreference.setOnPreferenceChangeListener(this);

    }
}
