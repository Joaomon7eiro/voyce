package com.android.voyce.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public final class ConnectivityHelper {
    private static final String TAG = ConnectivityHelper.class.getSimpleName();

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected =  networkInfo != null && networkInfo.isConnected();
        }
        if (isConnected) {
            Log.i(TAG, "Device connected to the internet");
        }

        return isConnected;
    }
}
