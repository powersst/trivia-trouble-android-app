package com.powersst.triviatrouble;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
{
    // MEMBERS
    private Bundle mSavedInstanceState;
    private Button mBtnBegin;
    private FloatingActionButton mFabLeaderboard;
    private FloatingActionButton mFabSettings;

    // METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "ON CREATE METHOD");

        // Capture references
        mSavedInstanceState = savedInstanceState;
        mBtnBegin = (Button) findViewById(R.id.btn_Main_Begin);


        // Assign actions
        mBtnBegin.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(v.getContext(), GameSetupActivity.class);
                startActivity(intent);
            }
        });


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Generates and displays a toast.
     * Ref: https://developer.android.com/guide/topics/ui/notifiers/toasts.html
     *
     * @author  Makiah Merritt <merrittm@oregonstate.edu>
     * @param   toastContext    context for the toast
     * @param   toastMessage    text for the toast
     * @param   toastDuration   toast's duration
     * @return  none
     */
    public void generateToast(Context toastContext, String toastMessage, int toastDuration)
    {
        Context context = (toastContext == null ? getApplicationContext() : toastContext);
        String message = (toastMessage == null ? "ERROR: No toast message provided" : toastMessage);
        int duration = (toastDuration == 0 ? Toast.LENGTH_LONG : toastDuration);
        Toast toast = Toast.makeText(context, message, duration);
        toast.setGravity(Gravity.BOTTOM, 0, -24);
        toast.show();
    } /*-- /generateToast() declaration --*/

    /**
     * Overloads generateToast, to only receive the message. Default duration
     * is Toast.LENGTH_LONG and default context is the applications.
     *
     * @author  Makiah Merritt <merrittm@oregonstate.edu>
     * @param   toastMessage
     * @return  none
     */
    public void generateToast(String toastMessage)
    {
        String message = (toastMessage == null ? "ERROR: No toast message provided" : toastMessage);
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    } /*-- /generateToast() declaration --*/
}
