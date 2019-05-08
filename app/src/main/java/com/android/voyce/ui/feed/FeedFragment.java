package com.android.voyce.ui.feed;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.voyce.R;
import com.android.voyce.data.model.Post;
import com.android.voyce.ui.NewPostActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FeedAdapter mAdapter;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        mRecyclerView = view.findViewById(R.id.feed_rv);

        mAdapter = new FeedAdapter();

        Button newPost = view.findViewById(R.id.new_post);
        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewPostActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        List<Post> posts = new ArrayList<>();
        posts.add(new Post("1", "asd", null, "", "asd", "2", 123));
        posts.add(new Post("1", "asd", null, "", "asd", "2", 123));
        posts.add(new Post("1", "asd", null, "", "asd", "2", 123));
        posts.add(new Post("1", "asd", null, "", "asd", "2", 123));
        mAdapter.setData(posts);

        return view;
    }

}
