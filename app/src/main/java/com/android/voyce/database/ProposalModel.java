package com.android.voyce.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

@Entity(tableName = "proposals")
public class ProposalModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String mId;

    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "musician_name")
    private String mMusicianName;

    @ColumnInfo(name = "musician_image")
    private Bitmap mMusicianImage;

    public ProposalModel(@NonNull String id, String name, String musicianName, Bitmap musicianImage) {
        mId = id;
        mName = name;
        mMusicianName = musicianName;
        mMusicianImage = musicianImage;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getMusicianName() {
        return mMusicianName;
    }

    public Bitmap getMusicianImage() {
        return mMusicianImage;
    }
}
