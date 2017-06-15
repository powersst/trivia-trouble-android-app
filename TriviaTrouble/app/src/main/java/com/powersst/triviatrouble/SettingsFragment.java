package com.powersst.triviatrouble;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by Makiah Merritt on 6/12/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String MAIN_ACTIVITY_KEY = "MainActivity";
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preferences);
//
//        /**
//         * Methodology http://codetheory.in/saving-user-settings-with-android-preferences/
//         */
//        for(int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
//            setPreferenceSummary(getPreferenceScreen().getPreference(i).getKey());
//        }
//    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        /**
         * Methodology http://codetheory.in/saving-user-settings-with-android-preferences/
         */
        for(int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            setPreferenceSummary(getPreferenceScreen().getPreference(i).getKey());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setPreferenceSummary(key);
    }

    /**
     * Methodology from https://stackoverflow.com/a/2888607
     */
    public void setPreferenceSummary(String key) {
        Preference pref = findPreference(key);
        String val = "";

        if (pref instanceof EditTextPreference) {
            val = ((EditTextPreference) pref).getText();
        } else if (pref instanceof ListPreference) {
            val = String.valueOf(((ListPreference) pref).getEntry());
        }
        pref.setSummary(val);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
