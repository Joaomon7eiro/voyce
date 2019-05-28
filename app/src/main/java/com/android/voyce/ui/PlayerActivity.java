package com.android.voyce.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import com.android.voyce.service.AudioPlayerService;
import com.android.voyce.R;
import com.android.voyce.data.model.Song;
import com.android.voyce.databinding.ActivityPlayerBinding;
import com.android.voyce.utils.PlayerServiceCallbacks;
import com.bumptech.glide.Glide;

public class PlayerActivity extends AppCompatActivity implements PlayerServiceCallbacks {

    private Intent mPlayerServiceIntent;
    private AudioPlayerService mAudioPlayerService;
    private ActivityPlayerBinding mBinding;

    private ServiceConnection mPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioPlayerService.PlayerBinder binder = (AudioPlayerService.PlayerBinder) service;
            mAudioPlayerService = binder.getService();
            mBinding.playerView.setPlayer(mAudioPlayerService.getPlayer());
            mAudioPlayerService.setCallback(PlayerActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAudioPlayerService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_player);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_player);

        mPlayerServiceIntent = new Intent(this, AudioPlayerService.class);
        bindService(mPlayerServiceIntent, mPlayerServiceConnection, BIND_AUTO_CREATE);

        mBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mPlayerServiceConnection);
        mBinding.playerView.setPlayer(null);
    }

    @Override
    public void updateUi(Song song) {
        if (song != null) {
            mBinding.songArtist.setText(song.getDescription());
            mBinding.songName.setText(song.getTitle());
            Glide.with(mBinding.getRoot()).load(song.getImage_url()).into(mBinding.songImage);
        }
    }
}
