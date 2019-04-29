package com.android.voyce.data.local;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final String TAG = AppExecutors.class.getSimpleName();
    private final Executor mDiskIO;
    private final Executor mNetworkIO;
    private static AppExecutors sInstance;

    private AppExecutors(Executor diskIO, Executor networkIO) {
        mDiskIO = diskIO;
        mNetworkIO = networkIO;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (AppExecutors.class) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3));
                Log.i(TAG, "App Executors created");
            }
        }
        return sInstance;
    }

    public Executor getDiskIO() {
        return mDiskIO;
    }

    public Executor getNetworkIO() {
        return mNetworkIO;
    }
}
