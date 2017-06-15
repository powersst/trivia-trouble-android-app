package com.powersst.triviatrouble.utils;

import android.net.Network;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by powersst on 5/10/17.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();



    private static final OkHttpClient mHTTPClient = new OkHttpClient();

    public static String doHTTPGet(String url) throws IOException {
        Log.d(TAG, "NetworkUtils doHTTPGet");

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = mHTTPClient.newCall(request).execute();

        try {
            return response.body().string();
        } finally {
            response.close();
        }
    }
}
