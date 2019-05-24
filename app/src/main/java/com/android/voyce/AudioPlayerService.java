package com.android.voyce;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.voyce.data.model.Song;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.utils.PlayerServiceCallbacks;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayerService extends Service {
    private List<Song> mSongList = new ArrayList<>();

    private SimpleExoPlayer mPlayer;
    private DataSource.Factory mDataSourceFactory;
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
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

        mDataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, getString(R.string.app_name)));

        mPlayer.addListener(new Player.EventListener() {

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

    public void startPlayerNotificationManager() {
        String channelId = "com.android.voyce";
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("playback_channel", "player", NotificationManager.IMPORTANCE_HIGH);
            channelId = notificationChannel.getId();
        }

        mPlayerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                mContext, channelId, R.string.channel_name, 1,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return mSongList.get(player.getCurrentWindowIndex()).getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intentActivity = new Intent(mContext, MainActivity.class);
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
                        startForeground(notificationId, notification);
                    }
                });

        mPlayerNotificationManager.setUseNavigationActionsInCompactView(true);
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

    private void concatSongs(int mChosenSongIndex) {
        mConcatenatingMediaSource = new ConcatenatingMediaSource();
        for (Song song : mSongList) {
            MediaSource mediaSource =
                    new ProgressiveMediaSource.Factory(mDataSourceFactory)
                            .createMediaSource(Uri.parse(song.getUrl()));
            mConcatenatingMediaSource.addMediaSource(mediaSource);
        }
        mPlayer.prepare(mConcatenatingMediaSource);
        if (mChosenSongIndex != -1) {
            mPlayer.seekTo(mChosenSongIndex, 0);
        }
        mPlayer.setPlayWhenReady(true);
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
        mPlayerHasError = false;
    }

    public void playSingles(List<Song> singles, String songId) {
        if (songId == null) {
            songId = "";
            mChosenSongIndex = -1;
        }
        mSongCount = 0;
        mSongList.clear();
        for (Song song : singles) {
            if (song.getId().equals(songId)) {
                mChosenSongIndex = mSongCount;
            }
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
                    concatSongs(mChosenSongIndex);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                mSongList.add(song);
                mSongCount += 1;
                if (mSongCount == querySize) {
                    concatSongs(mChosenSongIndex);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    public boolean hasError() {
        return mPlayerHasError;
    }

    public boolean serviceHasStarted() {
        return mServiceHasStarted;
    }
}
