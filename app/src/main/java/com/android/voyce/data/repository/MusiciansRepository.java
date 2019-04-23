package com.android.voyce.data.repository;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.Musician;
import com.android.voyce.data.remote.HttpClient;
import com.android.voyce.data.remote.WebService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MusiciansRepository {
    private static MusiciansRepository sInstance;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public static MusiciansRepository getInstance() {
        if (sInstance == null) {
            sInstance = new MusiciansRepository();
        }
        return sInstance;
    }

    public MutableLiveData<List<Musician>> getMusicians() {
        Retrofit client = HttpClient.getInstance();
        WebService webService = client.create(WebService.class);

        Call<List<Musician>> call = webService.getMusicians();

        final MutableLiveData<List<Musician>> data = new MutableLiveData<>();

        mIsLoading.setValue(true);
        call.enqueue(new Callback<List<Musician>>() {
            @Override
            public void onResponse(@NonNull Call<List<Musician>> call, @NonNull Response<List<Musician>> response) {
                mIsLoading.setValue(false);
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Musician>> call, @NonNull Throwable t) {
                mIsLoading.postValue(false);
            }
        });

        return data;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
