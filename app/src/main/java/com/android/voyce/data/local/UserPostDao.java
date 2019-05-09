package com.android.voyce.data.local;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.android.voyce.data.model.Post;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserPostDao {


    @Query("SELECT * FROM post WHERE current_user_id = :id ORDER BY timestamp DESC")
    LiveData<List<Post>> queryPosts(String id);

    @Insert(onConflict = REPLACE)
    void insertPosts(List<Post> postsList);

    @Query("SELECT COUNT(*) FROM post WHERE current_user_id = :id AND :timestamp - last_update_timestamp > :refreshDelay")
    int getUpdatedPostsCount(long timestamp, long refreshDelay, String id);
}
