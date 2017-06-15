package com.powersst.triviatrouble;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.powersst.triviatrouble.utils.NetworkUtils;
import com.powersst.triviatrouble.utils.OpenTriviaUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Makiah Merritt on 5/25/2017.
 */

public class GameSetupActivity extends AppCompatActivity {
    // MEMBERS
    private static final String KEY_GAME_OPTIONS = "selectedValues";    // Options from the activity's controls
    public static final String KEY_TRIVIA_ITEMS = "triviaItems";       // Trivia items returned
    private static final String TAG_SELF = GameSetupActivity.class.getSimpleName();
    private static final String TAG_PARENT = MainActivity.class.getSimpleName();
    private Bundle mSavedInstanceState;
    private Spinner mSpnQuestionCount;
    private Spinner mSpnQuestionCategory;
    private Spinner mSpnQuestionDifficulty;
    private Spinner mSpnQuestionType;
    private ProgressBar mPbLoading;
    private Button mBtnSearch;
    private Button mBtnBegin;
    private ArrayList<OpenTriviaUtils.TriviaItem> mTriviaItems;

    // METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Capture member references
        mSavedInstanceState = savedInstanceState;
        mSpnQuestionCount = (Spinner)findViewById(R.id.spn_GameSetup_QuestionCount);
        mSpnQuestionCategory = (Spinner)findViewById(R.id.spn_GameSetup_QuestionCategory);
        mSpnQuestionDifficulty = (Spinner)findViewById(R.id.spn_GameSetup_QuestionDifficulty);
        mSpnQuestionType = (Spinner)findViewById(R.id.spn_GameSetup_QuestionType);
        mPbLoading = (ProgressBar) findViewById(R.id.pb_GameSetup_Loading);
        mBtnSearch = (Button) findViewById(R.id.btn_GameSetup_Search);
        mBtnBegin = (Button) findViewById(R.id.btn_GameSetup_Begin);

        // Setup Button Actions
        mBtnSearch.setVisibility(View.VISIBLE);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOpenTriviaSearch();
            }
        });

        mBtnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //generateToast("__LOAD_GAME_ACTIVITY__");
                Intent intent = new Intent(v.getContext(), InGameActivity.class);
                intent.putExtra(KEY_TRIVIA_ITEMS, mTriviaItems);
                startActivity(intent);
            }
        });


//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String themeKey = sharedPreferences.getString(getString(R.string.preferences_theme_key), "");
//        int themeKeyInt;
//
//        if(themeKey == "AppTheme"){
//            setTheme(R.style.AppTheme);
//            generateToast("Theme Key = " + themeKey);
//        }
//        else if (themeKey == "AppThemeAlt"){
//            setTheme(R.style.AppThemeAlt);
//            generateToast("Theme Key = " + themeKey);
//        }


        initializeActivityElements();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /* Adding to the list breaks. However, this turned out not to be a problem because Android saves element selections itself; so we don't have to programmatically.

        // Capture selections
        ArrayList<Integer> selectedOptions = null;
        Log.d("GameSetupActivity", "Selected Options: "
                + mSpnQuestionCount.getSelectedItemPosition() + " "
                + mSpnQuestionCategory.getSelectedItemPosition() + " "
                + mSpnQuestionDifficulty.getSelectedItemPosition() + " "
                + mSpnQuestionType.getSelectedItemPosition()
        );
        selectedOptions.add(mSpnQuestionCount.getSelectedItemPosition());
        selectedOptions.add(mSpnQuestionCategory.getSelectedItemPosition());
        selectedOptions.add(mSpnQuestionDifficulty.getSelectedItemPosition());
        selectedOptions.add(mSpnQuestionType.getSelectedItemPosition());
        outState.putIntegerArrayList(KEY_GAME_OPTIONS, selectedOptions);
        */


        // Capture trivia items
        if(mTriviaItems != null) {
            outState.putSerializable(KEY_TRIVIA_ITEMS, mTriviaItems);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Prepares the dropdown elements and loads the previous options the player
     * has chosen.
     *
     * Notes: ref[https://developer.android.com/guide/topics/ui/controls/spinner.html]
     *
     * @author  firstPersonToWriteIt
     */
    private void initializeActivityElements() {
        ArrayAdapter<CharSequence> adapter = null;

        //==== Load spinners ====
        // Load Question Count
        adapter = ArrayAdapter.createFromResource(this, R.array.gameSetup_QuestionCount_Entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnQuestionCount.setAdapter(adapter);

        // Load Question Categories
        adapter = ArrayAdapter.createFromResource(this, R.array.gameSetup_QuestionCategory_Entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnQuestionCategory.setAdapter(adapter);

        // Load Question Difficulties
        adapter = ArrayAdapter.createFromResource(this, R.array.gameSetup_QuestionDifficulty_Entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnQuestionDifficulty.setAdapter(adapter);

        // Load Question Types
        adapter = ArrayAdapter.createFromResource(this, R.array.gameSetup_QuestionType_Entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnQuestionType.setAdapter(adapter);

        //==== Load players previous selections ====
        if(mSavedInstanceState != null && mSavedInstanceState.containsKey(KEY_TRIVIA_ITEMS))
        {
            mTriviaItems = (ArrayList<OpenTriviaUtils.TriviaItem>)mSavedInstanceState.getSerializable(KEY_TRIVIA_ITEMS);

            // Player can start the game
            mBtnBegin.setVisibility(View.VISIBLE);
        }
        mPbLoading.setVisibility(View.INVISIBLE);
    }


    //Execute Search
    public void doOpenTriviaSearch() {
        Log.d("GameSetupActivity", "doOpenTriviaSearch");

        String[] entryValues = null;
        String entryValue = null;
        String qAmount = null;
        String qCategory = null;
        String qDifficulty = null;
        String qType = null;

        qAmount = String.valueOf(mSpnQuestionCount.getSelectedItem());

        entryValues = getResources().getStringArray(R.array.gameSetup_QuestionCategory_EntryValues);
        entryValue = entryValues[mSpnQuestionCategory.getSelectedItemPosition()];
        qCategory = String.valueOf(entryValue);

        entryValues = getResources().getStringArray(R.array.gameSetup_QuestionDifficulty_EntryValues);
        entryValue = entryValues[mSpnQuestionDifficulty.getSelectedItemPosition()];
        qDifficulty = String.valueOf(entryValue);

        entryValues = getResources().getStringArray(R.array.gameSetup_QuestionType_EntryValues);
        entryValue = entryValues[mSpnQuestionType.getSelectedItemPosition()];
        qType = String.valueOf(entryValue);


        Log.d("GameSetupActivity", "Clearing saved items (if any).");
        mTriviaItems = null;
        Log.d("GameSetupActivity", "URL Params: " + qAmount + ", " + qCategory + ", " + qDifficulty + ", " + qType);
        String openTriviaSearchUrl = OpenTriviaUtils.buildTriviaURL(qAmount, qCategory, qDifficulty, qType);
        Log.d("GameSetupActivity", "Got search url: " + openTriviaSearchUrl);
        new OpenTriviaSearchTask().execute(openTriviaSearchUrl);
    }


    private class OpenTriviaSearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPbLoading.setVisibility(View.VISIBLE);
            mBtnSearch.setEnabled(false);
            mBtnBegin.setEnabled(false);
            mBtnBegin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String openWeatherSearchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHTTPGet(openWeatherSearchUrl);
                Log.d("GameSetupActivity", "searchResults " + searchResults);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            mPbLoading.setVisibility(View.INVISIBLE);
            mBtnSearch.setEnabled(true);

            if (s != null) {
                ArrayList<OpenTriviaUtils.TriviaItem> searchResultsList = OpenTriviaUtils.parseTriviaJSON(s);
                Log.d("GameSetupActivity", "Search Results list = " + searchResultsList);

                if(searchResultsList.size() == 0) {
                    generateToast(getResources().getString(R.string.msg_NoResults));
                }
                else {
                    generateToast(searchResultsList.size() + " / " + mSpnQuestionCount.getSelectedItem() + " questions found");
                    mBtnBegin.setText(getResources().getString(R.string.gameSetup_Begin_Text));
                    mTriviaItems = searchResultsList;

                    // Enable starting of game activity
                    mBtnBegin.setEnabled(true);
                    mBtnBegin.setVisibility(View.VISIBLE);
                }
            } else {
                generateToast(getResources().getString(R.string.gameSetup_LoadError));
            }
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
