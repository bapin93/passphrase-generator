package com.example.andres.passphrasegenerator;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

    private Preference mCustomStrengthPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference basicStrengthPreference = findPreference("passphrase_strength");
        basicStrengthPreference.setOnPreferenceChangeListener(this);
        mCustomStrengthPreference = findPreference("custom_strength");
        String[] strengthValues = getResources().getStringArray(R.array.strength_values);
        if(basicStrengthPreference.getSharedPreferences().getString(basicStrengthPreference.getKey(), strengthValues[1]).equals(strengthValues[4])) {
            mCustomStrengthPreference.setEnabled(true);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(getClass().getName(), "Got Click");
        boolean result = false;
        String[] strengthValues = getResources().getStringArray(R.array.strength_values);
        if (preference.getKey().equals("passphrase_strength")) {
            if (newValue.toString().equals(strengthValues[4])) {
                mCustomStrengthPreference.setEnabled(true);
                result = true;
                Log.d(getClass().getName(), "Set Custom");
            } else {
                mCustomStrengthPreference.setEnabled(false);
                result = true;
                Log.d(getClass().getName(), "Set Not Custom");
            }
        }
        return result;
    }
}
