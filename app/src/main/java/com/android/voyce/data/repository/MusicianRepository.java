package com.android.voyce.data.repository;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.voyce.data.model.Musician;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.remote.ApiWebService;
import com.android.voyce.data.remote.WebService;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MusicianRepository {
    private static MusicianRepository sInstance;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public static MusicianRepository getInstance() {
        if (sInstance == null) {
            sInstance = new MusicianRepository();
        }
        return sInstance;
    }

    public MutableLiveData<Musician> getMusician(String id) {
        Retrofit client = ApiWebService.getInstance();
        WebService webService = client.create(WebService.class);

        Call<Musician> call = webService.getMusician(id);

        final MutableLiveData<Musician> data = new MutableLiveData<>();

        mIsLoading.setValue(true);
        call.enqueue(new Callback<Musician>() {
            @Override
            public void onResponse(@NonNull Call<Musician> call, @NonNull Response<Musician> response) {
                mIsLoading.setValue(false);
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Musician> call, @NonNull Throwable t) {
                mIsLoading.setValue(false);
            }
        });
        return data;
    }

    public MutableLiveData<Proposal> getProposals(String id) {
        Retrofit client = ApiWebService.getInstance();
        WebService webService = client.create(WebService.class);

        Call<Proposal> call = webService.getMusicianProposals(id);

        final MutableLiveData<Proposal> data = new MutableLiveData<>();

        mIsLoading.setValue(true);
        call.enqueue(new Callback<Proposal>() {
            @Override
            public void onResponse(@NonNull Call<Proposal> call, @NonNull Response<Proposal> response) {
                mIsLoading.setValue(false);
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Proposal> call, @NonNull Throwable t) {
                mIsLoading.setValue(false);
            }
        });
        return data;
    }

//    public MutableLiveData<List<Proposal>> getProposals(String id) {
//        Retrofit client = ApiWebService.getInstance();
//        WebService webService = client.create(WebService.class);
//
//        Call<List<Proposal>> call = webService.getMusicianProposals(id);
//
//        final MutableLiveData<List<Proposal>> data = new MutableLiveData<>();
//
//        mIsLoading.setValue(true);
//        call.enqueue(new Callback<List<Proposal>>() {
//            @Override
//            public void onResponse(Call<List<Proposal>> call, Response<List<Proposal>> response) {
//                mIsLoading.setValue(false);
//                data.setValue(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<List<Proposal>> call, Throwable t) {
//                mIsLoading.setValue(false);
//            }
//        });
//        return data;
//    }

    public MutableLiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
