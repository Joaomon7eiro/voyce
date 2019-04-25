package com.android.voyce.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.AppExecutors;
import com.android.voyce.data.local.MusicianDao;
import com.android.voyce.data.model.Musician;
import com.android.voyce.data.remote.ApiWebService;
import com.android.voyce.data.remote.WebService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusiciansRepository {
    private static MusiciansRepository sInstance;
    private static Executor sExecutor;
    private static WebService sWebService;
    private static MusicianDao sMusicianDao;

    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public static MusiciansRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new MusiciansRepository();
            sWebService = ApiWebService.getWebService();
            sExecutor = AppExecutors.getInstance().getDiskIO();
            sMusicianDao = AppDatabase.getInstance(application).musicianDao();
        }
        return sInstance;
    }

    public LiveData<List<Musician>> getMusicians() {
        refreshUser();

        return sMusicianDao.getMusicians();
    }

    private void refreshUser() {
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                boolean needsRefresh = (sMusicianDao.getRefreshMusicians(timestamp.getTime()) > 0);
                if (needsRefresh) {
                    mIsLoading.postValue(true);
                    try {
                        Response<List<Musician>> response = sWebService.getMusicians().execute();
                        for (Musician musician: response.body()) {
                            musician.setLastUpdateTimestamp(timestamp.getTime());
                        }
                        sMusicianDao.insertMusicians(response.body());
                        mIsLoading.postValue(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }
}
