package com.android.voyce.data.model;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Song {
    @NonNull
    @PrimaryKey
    private String id;
    private String url;
    private String title;
    private String description;
    private String image_url;
    private String user_id;

    @Ignore
    private Bitmap bitmap;

    public Song(){

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String mediaId) {
        this.id = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Ignore
    public static MediaDescriptionCompat getMediaDescription(Song song) {
        Bundle extras = new Bundle();
        Bitmap bitmap = song.bitmap;
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap);
        extras.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.description);
        return new MediaDescriptionCompat.Builder()
                .setMediaId(song.id)
                .setIconBitmap(bitmap)
                .setTitle(song.title)
                .setDescription(song.description)
                .setExtras(extras)
                .build();
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
