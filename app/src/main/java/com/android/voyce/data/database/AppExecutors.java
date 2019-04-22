package com.android.voyce.data.database;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private final Executor mDiskIO;
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;

    private AppExecutors(Executor diskIO) {
        mDiskIO = diskIO;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor getDiskIO() {
        return mDiskIO;
    }
}