package com.powersst.triviatrouble;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by Makiah Merritt on 6/12/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {

                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        updateTheme();
                        recreate();
                    }
                }
        );
    }

    void updateTheme()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themeKey = sharedPreferences.getString(getString(R.string.preferences_theme_key), "");

        if(themeKey.equals("AppTheme")){
            setTheme(R.style.Default);
        }
        else if (themeKey.equals("AppThemeAlt")){
            setTheme(R.style.AppThemeAlt);
        }
        else{
            setTheme(R.style.Default);
        }
    }
}
