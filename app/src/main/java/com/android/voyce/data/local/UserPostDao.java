package com.android.voyce.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.voyce.data.model.Post;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserPostDao {


    @Query("SELECT * FROM post WHERE current_user_id = :id ORDER BY timestamp DESC")
    LiveData<List<Post>> queryPosts(String id);

    @Insert(onConflict = REPLACE)
    void insertPosts(List<Post> postsList);
}
