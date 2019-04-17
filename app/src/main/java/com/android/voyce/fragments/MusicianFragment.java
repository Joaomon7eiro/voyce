package com.android.voyce.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
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
import com.android.voyce.activities.MainActivity;
import com.android.voyce.adapters.MusicianFragmentPagerAdapter;
import com.android.voyce.loaders.MusicianFragmentLoader;
import com.android.voyce.models.Musician;
import com.android.voyce.models.MusicianAndProposals;
import com.android.voyce.utils.Constants;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianFragment extends Fragment implements LoaderManager.LoaderCallbacks<MusicianAndProposals> {

    private Button mFollowButton;
    private boolean isFollowing = false;

    private String mId;
    private String[] mTabsTitle;

    private ProgressBar mProgressBar;
    private AppBarLayout mAppBarLayout;
    private BlurImageView mBackgroundImage;
    private ImageView mProfileImage;
    private TextView mMusicianName;

    private MusicianFragmentPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private static final int LOADER_ID = 1;

    private View.OnClickListener mFollowOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isFollowing = !isFollowing;
            if (isFollowing) {
                mFollowButton.setBackground(getResources().getDrawable(R.drawable.rounded_background));
                mFollowButton.setText(getString(R.string.following));
            } else {
                mFollowButton.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
                mFollowButton.setText(getString(R.string.follow));
            }
        }
    };

    public MusicianFragment() {
    }

    public static MusicianFragment newInstance(Musician musician) {
        MusicianFragment fragment = new MusicianFragment();

        Bundle args = new Bundle();
        args.putSerializable(Constants.KEY_MUSICIAN_MAIN_INFO, musician);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            Musician musician = (Musician) args.getSerializable(Constants.KEY_MUSICIAN_MAIN_INFO);
            mId = musician.getId();
        }

        mTabsTitle = new String[]{getString(R.string.info_tab), getString(R.string.proposal_tab)};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_musician, container, false);

        mFollowButton = view.findViewById(R.id.follow_button);
        mFollowButton.setOnClickListener(mFollowOnClickListener);

        mProgressBar = view.findViewById(R.id.musician_details_progress_bar);
        mAppBarLayout = view.findViewById(R.id.musician_details_appbar);

        mViewPager = view.findViewById(R.id.view_pager);
        mTabLayout = view.findViewById(R.id.tab_layout);

        mBackgroundImage = view.findViewById(R.id.musician_background_image);
        mProfileImage = view.findViewById(R.id.musician_profile_image);
        mMusicianName = view.findViewById(R.id.musician_name);

        MainActivity activity = (MainActivity) getActivity();
        activity.checkInternetConnectivity();

        if (activity.isConnected()) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        return view;
    }

    @NonNull
    @Override
    public Loader<MusicianAndProposals> onCreateLoader(int i, @Nullable Bundle bundle) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAppBarLayout.setVisibility(View.GONE);

        String musicianBaseUrl = "https://5cb65ce3a3763800149fc8fd.mockapi.io/api/artists/" + mId;

        Uri musicianUrl = Uri.parse(musicianBaseUrl).buildUpon()
                .build();

        String proposalsBaseUrl = "https://5cb65ce3a3763800149fc8fd.mockapi.io/api/proposal/" + mId;
        Uri proposalsUrl = Uri.parse(proposalsBaseUrl).buildUpon()
                .build();

        return new MusicianFragmentLoader(getContext(), musicianUrl.toString(), proposalsUrl.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<MusicianAndProposals> loader, MusicianAndProposals musicianAndProposals) {
        mProgressBar.setVisibility(View.GONE);
        mAppBarLayout.setVisibility(View.VISIBLE);

        Musician musician = musicianAndProposals.getMusician();

        mPagerAdapter = new MusicianFragmentPagerAdapter(getChildFragmentManager(), mTabsTitle, musicianAndProposals);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        Picasso.get().load(musician.getImageUrl()).into(mBackgroundImage);
        Picasso.get().load(musician.getImageUrl()).into(mProfileImage);
        mBackgroundImage.setBlur(2);
        mMusicianName.setText(musician.getName());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<MusicianAndProposals> loader) {
        mPagerAdapter = null;
    }
}
