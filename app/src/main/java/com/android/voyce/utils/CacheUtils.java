package com.android.voyce.utils;

import android.content.Context;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;


public class CacheUtils {

    private static Cache sCache;
    private static ExoDatabaseProvider sExoInstance;

    private static final int TOTAL_CACHE_IN_MB = 20 * 1024 * 1024; // 20 mb

    public static synchronized Cache getCache(Context context) {
        if (sCache == null) {
            File cacheDirectory = new File(context.getCacheDir(), "media_cache");
            LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(TOTAL_CACHE_IN_MB);
            sCache = new SimpleCache(cacheDirectory, evictor, getExoInstance(context));
        }
        return sCache;
    }

    private static ExoDatabaseProvider getExoInstance(Context context) {
        if (sExoInstance == null) {
            sExoInstance = new ExoDatabaseProvider(context);
        }
        return sExoInstance;
    }
}
