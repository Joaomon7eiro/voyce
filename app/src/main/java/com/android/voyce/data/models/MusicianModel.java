package com.android.voyce.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

@Entity(tableName = "musicians")
public class MusicianModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String mId;
    @ColumnInfo(name = "name")
    private String mName;
    @ColumnInfo(name = "image")
    private Bitmap mImage;

    public MusicianModel(@NonNull String id, String name, Bitmap image) {
        mId = id;
        mName = name;
        mImage = image;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Bitmap getImage() {
        return mImage;
    }
}
