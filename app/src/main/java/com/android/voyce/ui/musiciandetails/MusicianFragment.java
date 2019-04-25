package com.android.voyce.ui.musiciandetails;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.AppExecutors;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.model.Musician;
import com.android.voyce.ui.MainActivity;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianFragment extends Fragment {

    private Button mFollowButton;
    private boolean isFollowing = false;

    private String mId;
    private String[] mTabsTitle;

    private ProgressBar mProgressBar;
    private AppBarLayout mAppBarLayout;
    private BlurImageView mBackgroundImage;
    private ImageView mProfileImage;
    private TextView mMusicianName;

    private UserFollowingMusician mUserFollowingMusician;
    private Bitmap mBitMapImage;
    private String mName;

    private ViewPager mViewPager;
    private AppDatabase mDb;

    private View.OnClickListener mFollowOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isFollowing = !isFollowing;
            Executor executor = AppExecutors.getInstance().getDiskIO();
            if (isFollowing) {
                mFollowButton.setBackground(getResources().getDrawable(R.drawable.rounded_background));
                mFollowButton.setText(getString(R.string.following));
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mUserFollowingMusician == null) {
                            mUserFollowingMusician = new UserFollowingMusician(mId, mName, mBitMapImage);
                        }
                        mDb.userFollowingMusicianDao().insertMusician(mUserFollowingMusician);
                    }
                });
            } else {
                mFollowButton.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
                mFollowButton.setText(getString(R.string.follow));
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.userFollowingMusicianDao().deleteMusician(mUserFollowingMusician);
                    }
                });
            }
        }
    };

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                if (ConnectivityHelper.isConnected(getContext())) {
                    getFragmentManager().popBackStack();
                } else {
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null) activity.setLayoutVisibility(false);
                }
            }
        }
    };

    public MusicianFragment() {
    }

    public static MusicianFragment newInstance(String id) {
        MusicianFragment fragment = new MusicianFragment();

        Bundle args = new Bundle();
        args.putString(Constants.KEY_MUSICIAN_ID, id);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            mId = args.getString(Constants.KEY_MUSICIAN_ID);
        }
        MusicianViewModel viewModel = ViewModelProviders.of(this).get(MusicianViewModel.class);
        viewModel.init(mId);
        viewModel.getMusician().observe(this, new Observer<Musician>() {
            @Override
            public void onChanged(@Nullable Musician musician) {
                if (musician != null) {
                    updateUi(musician);
                }
            }
        });
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null) {
                    if (isLoading) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mViewPager.setVisibility(View.GONE);
                        mAppBarLayout.setVisibility(View.GONE);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        mAppBarLayout.setVisibility(View.VISIBLE);
                        mViewPager.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mTabsTitle = new String[]{getString(R.string.info_tab), getString(R.string.proposal_tab)};
    }

    private void updateUi(Musician musician) {
        Picasso.get().load(musician.getImageUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mBitMapImage = bitmap;
                mBackgroundImage.setImageBitmap(mBitMapImage);
                mProfileImage.setImageBitmap(mBitMapImage);
                mBackgroundImage.setBlur(2);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });

        mName = musician.getName();
        mMusicianName.setText(mName);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_musician, container, false);

        ImageView backButton = view.findViewById(R.id.musician_back_button);
        backButton.setOnClickListener(mBackOnClickListener);

        mFollowButton = view.findViewById(R.id.follow_button);
        mFollowButton.setOnClickListener(mFollowOnClickListener);

        mProgressBar = view.findViewById(R.id.musician_details_progress_bar);
        mAppBarLayout = view.findViewById(R.id.musician_details_appbar);

        mViewPager = view.findViewById(R.id.view_pager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        mBackgroundImage = view.findViewById(R.id.musician_background_image);
        mProfileImage = view.findViewById(R.id.musician_profile_image);
        mMusicianName = view.findViewById(R.id.musician_name);

        MusicianFragmentPagerAdapter mPagerAdapter = new MusicianFragmentPagerAdapter(getChildFragmentManager(), mTabsTitle);
        mViewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        mDb = AppDatabase.getInstance(getContext());

        LiveData<UserFollowingMusician> musician = mDb.userFollowingMusicianDao().queryMusiciansById(mId);
        musician.observe(this, new Observer<UserFollowingMusician>() {
            @Override
            public void onChanged(@Nullable UserFollowingMusician userFollowingMusician) {
                mUserFollowingMusician = userFollowingMusician;
                if (mUserFollowingMusician != null) {
                    isFollowing = true;
                    mFollowButton.setBackground(getResources().getDrawable(R.drawable.rounded_background));
                    mFollowButton.setText(getString(R.string.following));
                }
            }
        });

        return view;
    }
}
