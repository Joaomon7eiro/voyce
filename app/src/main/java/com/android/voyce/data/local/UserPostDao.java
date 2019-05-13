package com.android.voyce.data.local;

import android.util.Log;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.android.voyce.data.model.Post;

import java.util.List;


import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class UserPostDao {


    @Query("SELECT * FROM post WHERE current_user_id = :id ORDER BY timestamp DESC")
    public abstract DataSource.Factory<Integer, Post> getPosts(String id);

    @Insert(onConflict = REPLACE)
    public abstract void insertPosts(List<Post> postsList);

    @Query("DELETE FROM post WHERE current_user_id = :uid")
    public abstract void deleteAllPosts(String uid);

    @Transaction
    public void updateData(List<Post> posts, String uid) {
        deleteAllPosts(uid);
        insertPosts(posts);
        Log.i("UserPostDao", "Feed data refreshed");
    }
}
