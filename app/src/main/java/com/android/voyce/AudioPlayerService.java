package com.android.voyce;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.voyce.data.model.Song;
import com.android.voyce.ui.PlayerActivity;
import com.android.voyce.utils.CacheUtils;
import com.android.voyce.utils.PlayerServiceCallbacks;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.voyce.utils.Constants.CHANNEL_ID;
import static com.android.voyce.utils.Constants.NOTIFICATION_ID;

public class AudioPlayerService extends Service {
    private List<Song> mSongList = new ArrayList<>();

    private SimpleExoPlayer mPlayer;
    private CacheDataSourceFactory mCachedDataSourceFactory;
    private ConcatenatingMediaSource mConcatenatingMediaSource;
    private PlayerNotificationManager mPlayerNotificationManager;

    private PlayerBinder mPlayerBinder = new PlayerBinder();

    private PlayerServiceCallbacks mPlayerServiceCallbacks;

    private int mCurrentIndex;
    private long mCurrentPosition = 0;

    private int mSongCount;
    private int mChosenSongIndex;

    private boolean mServiceHasStarted = false;
    private boolean mPlayerHasError = false;

    private Context mContext;
    private MediaSessionCompat mMediaSession;
    private MediaSessionConnector mMediaSessionConnector;
    private Intent mIntent;
    private Notification mNotification;
    private int mNotificationId;

    private String mSongId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mIntent = intent;
        return mPlayerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultTrackSelector());
        mPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, getString(R.string.app_name)));

        mCachedDataSourceFactory = new CacheDataSourceFactory(
                CacheUtils.getCache(mContext),
                dataSourceFactory,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

        mPlayer.addListener(new Player.EventListener() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    ContextWrapper contextWrapper = new ContextWrapper(mContext);
                    contextWrapper.startService(mIntent);
                    startForeground(mNotificationId, mNotification);
                } else {
                    stopForeground(false);
                }
            }

            @Override
            public void onSeekProcessed() {
                mCurrentPosition = mPlayer.getCurrentPosition();
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                error.printStackTrace();
                mPlayerHasError = true;
                mCurrentPosition = mPlayer.getCurrentPosition();
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                if (mPlayer.getCurrentWindowIndex() != mCurrentIndex) {
                    mCurrentIndex = mPlayer.getCurrentWindowIndex();
                    mPlayerServiceCallbacks.updateUi(mSongList.get(mCurrentIndex));
                }
            }
        });
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        mServiceHasStarted = true;
        if (mPlayerNotificationManager == null) {
            startPlayerNotificationManager();
        }
        return START_STICKY;
    }

    public class CustomActionReceiver implements PlayerNotificationManager.CustomActionReceiver {
        static final String LIKE_ACTION = "likeAction";

        @Override
        public Map<String, NotificationCompat.Action> createCustomActions(Context context, int instanceId) {
            Intent intent = new Intent(LIKE_ACTION).setPackage(context.getPackageName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, instanceId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Action action = new NotificationCompat.Action(
                    R.drawable.ic_thumbs_up, LIKE_ACTION, pendingIntent);

            Map<String, NotificationCompat.Action> map = new HashMap<>();
            map.put(LIKE_ACTION, action);
            return map;
        }

        @Override
        public List<String> getCustomActions(Player player) {
            List<String> customActions = new ArrayList<>();
            customActions.add(LIKE_ACTION);
            return customActions;
        }

        @Override
        public void onCustomAction(Player player, String action, Intent intent) {
            switch (action) {
                case LIKE_ACTION:
                    Toast.makeText(
                            getApplicationContext(),
                            "Curtiu " + mSongList.get(mPlayer.getCurrentWindowIndex()).getTitle(),
                            Toast.LENGTH_SHORT).show();
                default:
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startPlayerNotificationManager() {
        createNotificationChannel();
        mPlayerNotificationManager = new PlayerNotificationManager(
                mContext, CHANNEL_ID, NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return mSongList.get(player.getCurrentWindowIndex()).getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intentActivity = new Intent(mContext, PlayerActivity.class);
                        return PendingIntent.getActivity(mContext, 0,
                                intentActivity, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return mSongList.get(player.getCurrentWindowIndex()).getDescription();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        return mSongList.get(player.getCurrentWindowIndex()).getBitmap();
                    }
                }
                , new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                if (dismissedByUser) {
                    stopSelf();
                }
            }

            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                mNotification = notification;
                mNotificationId = notificationId;
                if (ongoing) {
                    startForeground(notificationId, notification);
                }
            }
        }, new CustomActionReceiver());

        mPlayerNotificationManager.setRewindIncrementMs(0);
        mPlayerNotificationManager.setFastForwardIncrementMs(0);

        mPlayerNotificationManager.setUseNavigationActionsInCompactView(true);
        mMediaSession = new MediaSessionCompat(mContext, "voyce");
        mMediaSession.setActive(true);

        mPlayerNotificationManager.setMediaSessionToken(mMediaSession.getSessionToken());

        mMediaSessionConnector = new MediaSessionConnector(mMediaSession);

        mMediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mMediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return Song.getMediaDescription(mSongList.get(windowIndex));
            }
        });

        mMediaSessionConnector.setPlayer(mPlayer);
    }

    @Override
    public void onDestroy() {
        if (mPlayerNotificationManager != null) {
            mPlayerNotificationManager.setPlayer(null);
        }
        mPlayer.release();
        mPlayer = null;
        mServiceHasStarted = false;
        super.onDestroy();
    }

    public void playSingles(List<Song> singles, String songId) {
        mSongId = songId;
        if (mSongId == null) {
            mSongId = "";
        }
        mSongCount = 0;
        mSongList.clear();
        for (Song song : singles) {
            downloadBitmap(song, singles.size());
        }
    }

    void downloadBitmap(final Song song, final int querySize) {
        Glide.with(mContext).asBitmap().load(song.getImage_url()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                song.setBitmap(resource);
                mSongList.add(song);
                mSongCount += 1;
                if (mSongCount == querySize) {
                    concatSongs();
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                mSongList.add(song);
                mSongCount += 1;
                if (mSongCount == querySize) {
                    concatSongs();
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    private void concatSongs() {
        mConcatenatingMediaSource = new ConcatenatingMediaSource();
        for (Song song : mSongList) {
            MediaSource mediaSource =
                    new ProgressiveMediaSource.Factory(mCachedDataSourceFactory)
                            .createMediaSource(Uri.parse(song.getUrl()));
            mConcatenatingMediaSource.addMediaSource(mediaSource);
        }
        for (int i = 0; i < mSongList.size(); i++) {
            if (mSongList.get(i).getId().equals(mSongId)) {
                mChosenSongIndex = i;
            }
        }

        mPlayer.prepare(mConcatenatingMediaSource);
        mPlayer.setPlayWhenReady(true);
        if (mChosenSongIndex != -1) {
            mPlayer.seekTo(mChosenSongIndex, 0);
        }

        mPlayerNotificationManager.setPlayer(mPlayer);

        mCurrentIndex = mPlayer.getCurrentWindowIndex();
        mPlayerServiceCallbacks.updateUi(mSongList.get(mCurrentIndex));
    }

    public void setCallback(PlayerServiceCallbacks callback) {
        mPlayerServiceCallbacks = callback;
        Song song = null;
        if (mSongList.size() > 0) {
            song = mSongList.get(mPlayer.getCurrentWindowIndex());
        }
        mPlayerServiceCallbacks.updateUi(song);
    }

    public SimpleExoPlayer getPlayer() {
        return mPlayer;
    }

    public class PlayerBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    public void resumePlayer() {
        mPlayer.prepare(mConcatenatingMediaSource);
        mPlayer.seekTo(mCurrentIndex, mCurrentPosition);
        mPlayer.setPlayWhenReady(true);
        mPlayerNotificationManager.setPlayer(mPlayer);
        mPlayerNotificationManager.setMediaSessionToken(mMediaSession.getSessionToken());
        mMediaSessionConnector.setPlayer(mPlayer);
        mPlayerHasError = false;
    }

    public boolean hasError() {
        return mPlayerHasError;
    }

    public boolean serviceHasStarted() {
        return mServiceHasStarted;
    }
}
