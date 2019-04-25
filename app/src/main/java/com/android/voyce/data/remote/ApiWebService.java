package com.android.voyce.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiWebService {

    private static final String BASE_URL = "https://5cb65ce3a3763800149fc8fd.mockapi.io/api/";
    private static Retrofit sInstance;
    private static WebService sWebService;

    public static Retrofit getInstance() {
        if (sInstance == null) {
            sInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sInstance;
    }

    public static WebService getWebService() {
        if (sInstance == null) {
            sInstance = getInstance();
        }
        if (sWebService == null) {
            sWebService = sInstance.create(WebService.class);
        }

        return sWebService;
    }
}
