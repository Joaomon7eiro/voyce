package com.android.voyce.data.local;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.voyce.data.model.Post;

import java.util.List;


import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserPostDao {


    @Query("SELECT * FROM post WHERE current_user_id = :id ORDER BY timestamp DESC")
    DataSource.Factory<Integer, Post> getPosts(String id);

    @Query("SELECT * FROM post WHERE current_user_id = :id GROUP BY user_id ORDER BY timestamp DESC")
    List<Post> getPostsIdAndTimestamp(String id);

    @Insert(onConflict = REPLACE)
    void insertPosts(List<Post> postsList);
}
