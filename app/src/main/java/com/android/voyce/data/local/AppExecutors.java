package com.android.voyce.data.local;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
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
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
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
