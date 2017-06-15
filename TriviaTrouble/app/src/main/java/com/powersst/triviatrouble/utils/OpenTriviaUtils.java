package com.powersst.triviatrouble.utils;

import android.net.Uri;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static android.util.Base64.decode;

/**
 * Created by powersst on 5/23/17.
 */

public class OpenTriviaUtils {
    private final static String OPEN_TRIVIA_BASE_URL = "https://opentdb.com/api.php?";
    private final static String OPEN_TRIVIA_AMOUNT_PARAM = "amount";
    private final static String OPEN_TRIVIA_CATEGORY_PARAM = "category";
    private final static String OPEN_TRIVIA_DIFFICULTY_PARAM = "difficulty";
    private final static String OPEN_TRIVIA_TYPE_PARAM = "type";
    private final static String OPEN_TRIVIA_ENCODING_PARAM = "encode";


    private final static String qAmount = "10";
    private final static String qCategory = "9";
    private final static String qDifficulty = "easy";
    private final static String qType = "multiple";
    //private final static String qEncode = "url3986";
    private final static String qEncode = "base64";

/*
 * Layout for typical MC question
 * ref[https://opentdb.com/api.php?amount=10&category=9&difficulty=easy&type=multiple]
 *
 *    "category": "General Knowledge",
 *    "type": "multiple",
 *    "difficulty": "easy",
 *    "question": "Virgin Trains, Virgin Atlantic and Virgin Racing, are all companies owned by which famous entrepreneur?   ",
 *    "correct_answer": "Richard Branson",
 *    "incorrect_answers": [
 *        "Alan Sugar",
 *        "Donald Trump",
 *        "Bill Gates"
 *        ]
 *
 * Layout for a typical TF question
 * ref[https://opentdb.com/api.php?amount=10&category=9&difficulty=easy&type=boolean]
 *
 *    "category": "General Knowledge",
 *    "type": "boolean",
 *    "difficulty": "easy",
 *    "question": "When you cry in space, your tears stick to your face.",
 *    "correct_answer": "True",
 *    "incorrect_answers": ["False"]
 */

    public static class TriviaItem implements Serializable {
        /* for later implementation of intents
        public static final String EXTRA_TRIVIA_ITEM = "com.powersst.triviatrouble.utils.TriviaItem.SearchResult";
        */

        public byte[] category;
        public byte[] type;
        public byte[] difficulty;
        public byte[] question;
        public byte[] correct_answer;
        public String[] incorrect_answers;
    }

    public static String buildTriviaURL(String qAmount, String qCategory, String qDifficulty, String qType) {
        return Uri.parse(OPEN_TRIVIA_BASE_URL).buildUpon()
                .appendQueryParameter(OPEN_TRIVIA_AMOUNT_PARAM, qAmount)
                .appendQueryParameter(OPEN_TRIVIA_CATEGORY_PARAM, qCategory)
                .appendQueryParameter(OPEN_TRIVIA_DIFFICULTY_PARAM, qDifficulty)
                .appendQueryParameter(OPEN_TRIVIA_TYPE_PARAM, qType)
                .appendQueryParameter(OPEN_TRIVIA_ENCODING_PARAM, qEncode)
                .build()
                .toString();
    }

    public static ArrayList<TriviaItem> parseTriviaJSON(String triviaJSON) {
        try {
            JSONObject triviaObj = new JSONObject(triviaJSON);
            JSONArray triviaList = triviaObj.getJSONArray("results");

            ArrayList<TriviaItem> triviaItemsList = new ArrayList<TriviaItem>();
            for (int i = 0; i < triviaList.length(); i++) {
                TriviaItem triviaItem = new TriviaItem();
                JSONObject triviaListElem = triviaList.getJSONObject(i);

                triviaItem.category = decode(triviaListElem.getString("category"), Base64.DEFAULT);
                triviaItem.type = decode(triviaListElem.getString("type"), Base64.DEFAULT);
                triviaItem.difficulty = decode(triviaListElem.getString("difficulty"), Base64.DEFAULT);
                triviaItem.question = decode(triviaListElem.getString("question"), Base64.DEFAULT);
                triviaItem.correct_answer = decode(triviaListElem.getString("correct_answer"), Base64.DEFAULT);

                JSONArray triviaIncorrect = triviaListElem.getJSONArray("incorrect_answers");
                triviaItem.incorrect_answers = new String[triviaIncorrect.length()];

                for (int j = 0; j < triviaIncorrect.length(); j++) {
                    triviaItem.incorrect_answers[j] = new String(decode(triviaIncorrect.getString(j), Base64.DEFAULT));
                }

                triviaItemsList.add(triviaItem);
            }
            return triviaItemsList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
