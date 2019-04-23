package com.android.voyce.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class HttpClient {

    private static final String BASE_URL = "https://5cb65ce3a3763800149fc8fd.mockapi.io/api/";
    private static Retrofit sInstance;

    public static Retrofit getInstance() {
        if (sInstance == null) {
            sInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sInstance;
    }
}
