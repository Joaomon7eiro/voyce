package com.android.voyce.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.adapters.SimpleFragmentPagerAdapter;
import com.android.voyce.models.Musician;
import com.android.voyce.utils.NetworkUtils;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianFragment extends Fragment implements LoaderManager.LoaderCallbacks<Musician> {

    Button mFollowButton;
    boolean isFollowing = false;
    ProgressBar mProgressBar;
    String mName;
    SimpleFragmentPagerAdapter mPagerAdapter;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    String[] mTabsTitle = new String[]{"Info", "Planos"};
    private static final int LOADER_ID = 1;
    Musician mMusician;
    AppBarLayout mAppBarLayout;

    public MusicianFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle args = this.getArguments();

        String imageUrl = args.getString("image_url");
        mName = args.getString("name");

        View view = inflater.inflate(R.layout.fragment_musician, container, false);

        mFollowButton = view.findViewById(R.id.follow_button);
        mProgressBar = view.findViewById(R.id.musician_details_progress_bar);

        mAppBarLayout = view.findViewById(R.id.musician_details_appbar);
        mViewPager = view.findViewById(R.id.view_pager);
        mTabLayout = view.findViewById(R.id.tab_layout);

        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFollowing = !isFollowing;
                if (isFollowing) {
                    mFollowButton.setBackground(getResources().getDrawable(R.drawable.rounded_background));
                    mFollowButton.setText(getString(R.string.following));
                } else {
                    mFollowButton.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
                    mFollowButton.setText(getString(R.string.follow));
                }
            }
        });

        BlurImageView backgroundImage = view.findViewById(R.id.musician_background_image);
        ImageView profileImage = view.findViewById(R.id.musician_profile_image);
        TextView musicianName = view.findViewById(R.id.musician_name);

        Picasso.get().load(imageUrl).into(profileImage);
        Picasso.get().load(imageUrl).into(backgroundImage);
        backgroundImage.setBlur(2);

        musicianName.setText(mName);

        getLoaderManager().initLoader(LOADER_ID, null, this);
        return view;
    }

    @NonNull
    @Override
    public Loader<Musician> onCreateLoader(int i, @Nullable Bundle bundle) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAppBarLayout.setVisibility(View.GONE);
        String baseUrl = "http://ws.audioscrobbler.com/2.0/";
        final Uri uri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("method", "artist.getinfo")
                .appendQueryParameter("api_key", getString(R.string.api_key))
                .appendQueryParameter("format", "json")
                .appendQueryParameter("artist", mName)
                .build();

        final AsyncTaskLoader<Musician> asyncTaskLoader = new AsyncTaskLoader(getContext()) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Musician loadInBackground() {
                return NetworkUtils.fetchMusicianDetailsData(uri.toString());
            }
        };
        return asyncTaskLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Musician> loader, Musician musician) {
        mProgressBar.setVisibility(View.GONE);
        mAppBarLayout.setVisibility(View.VISIBLE);

        mMusician = musician;
        mPagerAdapter = new SimpleFragmentPagerAdapter(
                getChildFragmentManager(), mTabsTitle, mMusician);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Musician> loader) {
        mMusician = null;
    }
}
