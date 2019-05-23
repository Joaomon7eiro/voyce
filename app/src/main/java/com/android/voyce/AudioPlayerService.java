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

import androidx.annotation.Nullable;

import com.android.voyce.data.model.Song;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.utils.PlayerServiceCallbacks;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayerService extends Service {

    private SimpleExoPlayer mPlayer;
    private List<Song> mSongList = new ArrayList<>();
    private DataSource.Factory mDataSourceFactory;
    private PlayerNotificationManager mPlayerNotificationManager;
    private PlayerBinder mPlayerBinder = new PlayerBinder();
    private int mCurrentIndex;
    private PlayerServiceCallbacks mPlayerServiceCallbacks;
    private boolean mServiceHasStarted = false;

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
        Context context = this;
        mPlayer = ExoPlayerFactory.newSimpleInstance(context);
        mDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "Voyce"));

        mPlayer.addListener(new Player.EventListener() {
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
        final Context context = this;

        mServiceHasStarted = true;

        NotificationChannel notificationChannel;
        String channelId = "com.android.voyce";
        if (Build.VERSION.SDK_INT >= 26) {
            notificationChannel = new NotificationChannel("playback_channel", "player", NotificationManager.IMPORTANCE_HIGH);
            channelId = notificationChannel.getId();
        }

        mPlayerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context, channelId, R.string.channel_name, 1,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return mSongList.get(player.getCurrentWindowIndex()).getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intentActivity = new Intent(context, MainActivity.class);
                        return PendingIntent.getActivity(context, 0,
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
                        stopSelf();
                    }

                    @Override
                    public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                        startForeground(notificationId, notification);
                    }
                });

        mPlayerNotificationManager.setPlayer(mPlayer);

        return START_STICKY;
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



    public void setCallback(PlayerServiceCallbacks callback) {
        if (mPlayerServiceCallbacks == null) {
            mPlayerServiceCallbacks = callback;
        }
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
        int mSongCount;
        int mChosenSongIndex;
        Target mTarget;

        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }

        void downloadBitmap(final Song song, final int querySize) {
            mTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    song.setBitmap(bitmap);
                    mSongList.add(song);
                    mSongCount += 1;
                    if (mSongCount == querySize) {
                        concatSongs(mChosenSongIndex);
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    mSongList.add(song);
                    mSongCount += 1;
                    if (mSongCount == querySize) {
                        concatSongs(mChosenSongIndex);
                    }
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            Picasso.get().load(song.getImage_url()).into(mTarget);
        }

        public void playSingles(String userId, String songId) {
            final CollectionReference reference = FirebaseFirestore.getInstance()
                    .collection("music")
                    .document(userId)
                    .collection("singles");

            if (songId != null) {
                addSingles(reference, songId);
            } else {
                addSingles(reference, "");
            }
        }

        public boolean serviceHasStarted() {
            return mServiceHasStarted;
        }

        private void addSingles(CollectionReference reference, final String mediaId) {
            mSongCount = 0;
            if (mediaId.equals("")) {
                mChosenSongIndex = -1;
            }
            reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                    mSongList.clear();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Song song = snapshot.toObject(Song.class);
                        if (!song.getId().equals(mediaId)) {
                            downloadBitmap(song, queryDocumentSnapshots.size());
                        } else {
                            mChosenSongIndex = mSongCount;
                        }
                    }
                }
            });
        }

        private void concatSongs(int mChosenSongIndex) {
            ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
            for (Song song : mSongList) {
                MediaSource mediaSource =
                        new ProgressiveMediaSource.Factory(mDataSourceFactory)
                                .createMediaSource(Uri.parse(song.getUrl()));
                concatenatingMediaSource.addMediaSource(mediaSource);
            }
            mPlayer.prepare(concatenatingMediaSource);
            if (mChosenSongIndex != -1) {
                mPlayer.seekTo(mChosenSongIndex, 0);
            }
            mPlayer.setPlayWhenReady(true);
            mCurrentIndex = mPlayer.getCurrentWindowIndex();
            mPlayerServiceCallbacks.updateUi(mSongList.get(mCurrentIndex));
        }
    }
}
