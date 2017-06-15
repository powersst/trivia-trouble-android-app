package com.powersst.triviatrouble;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.powersst.triviatrouble.utils.OpenTriviaUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by User on 5/30/2017.
 */

public class InGameActivity extends AppCompatActivity {

    private TextView mTitleTV;
    private TextView mQuestionTV;
    private Button mBtnOptOne;
    private Button mBtnOptTwo;
    private Button mBtnOptThree;
    private Button mBtnOptFour;
    private Button mBtnSubmit;
    private Button mBtnNext;
    private Button mBtnFinish;
    private int mSelectedAnswer;
    private int mCorrectAnswer;
    private ArrayList<OpenTriviaUtils.TriviaItem> mTriviaItems;
    private int mCurrentTriviaItem;
    private static int playerScore;
    private static int totalScore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_in_game);

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String themeKey = sharedPreferences.getString(getString(R.string.preferences_theme_key), "");
//
//        Log.d("InGameActivity", themeKey);
//
//        LinearLayout bgElement = (LinearLayout) findViewById(R.id.container);
//
//        bgElement.setBackgroundColor(getResources().getColor(R.color.colorPrimaryAlt));




        mTitleTV = (TextView) findViewById(R.id.titleTV);
        mQuestionTV = (TextView) findViewById(R.id.questionTV);
        mBtnOptOne = (Button) findViewById(R.id.optionOneBtn);
        mBtnOptTwo = (Button) findViewById(R.id.optionTwoBtn);
        mBtnOptThree = (Button) findViewById(R.id.optionThreeBtn);
        mBtnOptFour = (Button) findViewById(R.id.optionFourBtn);
        mBtnSubmit = (Button) findViewById(R.id.submitBtn);
        mBtnFinish = (Button) findViewById(R.id.finishBtn);
        playerScore = 0;
        totalScore = 1;
            mBtnSubmit.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    checkAnswer();
                }
            });

        mBtnNext = (Button) findViewById(R.id.nextBtn);
            mBtnNext.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    loadQuestion(mTriviaItems.get(mCurrentTriviaItem));
                    totalScore = totalScore + 1 ;
                }
            });
        mBtnFinish.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EndGameActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        mTriviaItems = (ArrayList<OpenTriviaUtils.TriviaItem>) intent.getSerializableExtra(GameSetupActivity.KEY_TRIVIA_ITEMS);
        mCurrentTriviaItem = 0;
        mCorrectAnswer = -999;
        initializeLayout();
    }

    /**
     * Performs setup for the forms buttons (hide all and set on click) and then loads the first
     * question in the mTriviaItems list.
     *
     * @requires    onClickListener_AnswerButtonPressed()
     * @requires    onClickListener_ResetAllAnswerButtons()
     */
    private void initializeLayout()
    {
        /*
         * Setup buttons:
         * - visibilities
         * - onClickListeners
         */
        mBtnOptOne.setVisibility(View.INVISIBLE);
        mBtnOptOne.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickListener_AnswerButtonPressed(mBtnOptOne);
            }
        });

        mBtnOptTwo.setVisibility(View.INVISIBLE);
        mBtnOptTwo.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickListener_AnswerButtonPressed(mBtnOptTwo);
            }
        });

        mBtnOptThree.setVisibility(View.INVISIBLE);
        mBtnOptThree.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickListener_AnswerButtonPressed(mBtnOptThree);
            }
        });

        mBtnOptFour.setVisibility(View.INVISIBLE);
        mBtnOptFour.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickListener_AnswerButtonPressed(mBtnOptFour);
            }
        });


        /*
         * Load the first question
         */
        loadQuestion(mTriviaItems.get(mCurrentTriviaItem));
    }

    private void onClickListener_AnswerButtonPressed(Button btn) {
        // Check to see the buttons current state
        if(btn.getId() == mSelectedAnswer)
        {   // button is pressed
            // show as un-pressed
            answerButtonSetUnPressed(btn);
            mSelectedAnswer = -999;
        }
        else
        {   // button isn't pressed
            // show as pressed.
            answerButtonSetUnPressed(mBtnOptOne);
            answerButtonSetUnPressed(mBtnOptTwo);
            answerButtonSetUnPressed(mBtnOptThree);
            answerButtonSetUnPressed(mBtnOptFour);
            answerButtonSetPressed(btn);

            // set as selected, for use in checkAnswer()
            mSelectedAnswer = btn.getId();
        }
    }

    private void resetButtons() {
        answerButtonSetUnPressed(mBtnOptOne);
        answerButtonSetUnPressed(mBtnOptTwo);
        answerButtonSetUnPressed(mBtnOptThree);
        answerButtonSetUnPressed(mBtnOptFour);
        mSelectedAnswer = -999;

        mBtnOptOne.setText("");
        mBtnOptTwo.setText("");
        mBtnOptThree.setText("");
        mBtnOptFour.setText("");

        mBtnOptOne.setVisibility(View.INVISIBLE);
        mBtnOptTwo.setVisibility(View.INVISIBLE);
        mBtnOptThree.setVisibility(View.INVISIBLE);
        mBtnOptFour.setVisibility(View.INVISIBLE);

        mBtnOptOne.setClickable(true);
        mBtnOptTwo.setClickable(true);
        mBtnOptThree.setClickable(true);
        mBtnOptFour.setClickable(true);
        mBtnSubmit.setClickable(true);
    }

    private void answerButtonSetPressed(Button btn) {
        btn.setBackgroundResource(R.color.colorAnswerButton_Pressed);
    }

    private void answerButtonSetUnPressed(Button btn) {
        btn.setBackgroundResource(android.R.drawable.btn_default);
    }

    /**
     * ref[https://stackoverflow.com/a/3732080]
     */
    private void loadQuestion(OpenTriviaUtils.TriviaItem triviaItem)
    {
        List<String> answers = new ArrayList<String>();

        /*
         * Setup display
         */
        resetButtons();
        mTitleTV.setText("Question: " + (mCurrentTriviaItem + 1) + " of " + mTriviaItems.size());
        String question = new String (triviaItem.question);
        mQuestionTV.setText(question);

        /*
         * Handle Answers
         */
        // Load answers and shuffle
        String correctAnswer = new String(triviaItem.correct_answer);
        answers.add(correctAnswer);
        for(int i = 0; i < triviaItem.incorrect_answers.length; i++) {
            answers.add(triviaItem.incorrect_answers[i]);
        }
        Collections.shuffle(answers);

        // Set answer button text values
        for(int i = 0; i < answers.size(); i++) {
            switch(i)
            {
                case 0:
                    if(answers.get(i).equals(correctAnswer)) {
                        mCorrectAnswer = mBtnOptOne.getId();
                    }

                    mBtnOptOne.setText(answers.get(i));
                    mBtnOptOne.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    if(answers.get(i).equals(correctAnswer)) {
                        mCorrectAnswer = mBtnOptTwo.getId();
                    }

                    mBtnOptTwo.setText(answers.get(i));
                    mBtnOptTwo.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    if(answers.get(i).equals(correctAnswer)) {
                        mCorrectAnswer = mBtnOptThree.getId();
                    }

                    mBtnOptThree.setText(answers.get(i));
                    mBtnOptThree.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    if(answers.get(i).equals(correctAnswer)) {
                        mCorrectAnswer = mBtnOptFour.getId();
                    }

                    mBtnOptFour.setText(answers.get(i));
                    mBtnOptFour.setVisibility(View.VISIBLE);
                    break;
            }
        }
        mBtnSubmit.setVisibility(View.VISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
    }

    private void checkAnswer()
    {

        if(mSelectedAnswer != -999) {
            // Display result
            Button correctAnswer = (Button) findViewById(mCorrectAnswer);
            Button selectedAnswer = (Button) findViewById(mSelectedAnswer);
            if (selectedAnswer.equals(correctAnswer)) {
                correctAnswer.setBackgroundResource(R.color.colorAnswerButton_CorrectAnswer);
                playerScore = playerScore + 1;
                generateToast("Correct: " + playerScore + "/" + totalScore );
            } else {
                correctAnswer.setBackgroundResource(R.color.colorAnswerButton_CorrectAnswer);
                selectedAnswer.setBackgroundResource(R.color.colorAnswerButton_IncorrectAnswer);
                generateToast("Incorrect: " + playerScore + "/" + totalScore );
            }


            // Update score


            // Change button states
            mBtnOptOne.setClickable(false);
            mBtnOptTwo.setClickable(false);
            mBtnOptThree.setClickable(false);
            mBtnOptFour.setClickable(false);
            mBtnSubmit.setClickable(false);

            // Show next options
            if (++mCurrentTriviaItem < mTriviaItems.size()) {
                mBtnNext.setVisibility(View.VISIBLE);
                mBtnSubmit.setVisibility(View.INVISIBLE);
            } else {
                mBtnFinish.setVisibility(View.VISIBLE);
                mBtnFinish.setClickable(true);
            }
        }
    }

    public static int getPlayerScore() {
        return playerScore;
    }

    public static int getTotalScore() {
        //totalScore = mTriviaItems.size();
        return totalScore;
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