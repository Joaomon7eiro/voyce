package com.android.voyce.ui.musiciandetails;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.ui.main.MainActivity;
import com.android.voyce.utils.ConnectivityHelper;
import com.android.voyce.utils.Constants;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianFragment extends Fragment {

    private Button mFollowButton;

    private String mId;
    private String mName;
    private String mImage;
    private Bitmap mBitMapImage;

    private String mUserId;
    private String mUserName;
    private String mUserImage;

    private String[] mTabsTitle;

    private ProgressBar mProgressBar;
    private AppBarLayout mAppBarLayout;
    private BlurImageView mBackgroundImage;
    private ImageView mProfileImage;
    private TextView mMusicianName;


    private ViewPager mViewPager;

    private MusicianViewModel mViewModel;

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

    public static MusicianFragment newInstance(String id, String name, String image) {
        MusicianFragment fragment = new MusicianFragment();

        Bundle args = new Bundle();
        args.putString(Constants.KEY_MUSICIAN_ID, id);
        args.putString(Constants.KEY_MUSICIAN_NAME, name);
        args.putString(Constants.KEY_MUSICIAN_IMAGE, image);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            mId = args.getString(Constants.KEY_MUSICIAN_ID);
            mName = args.getString(Constants.KEY_MUSICIAN_NAME);
            mImage = args.getString(Constants.KEY_MUSICIAN_IMAGE);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserId = sharedPreferences.getString(Constants.KEY_CURRENT_USER_ID, null);

        mTabsTitle = new String[]{getString(R.string.info_tab), getString(R.string.proposal_tab)};
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserFollowingMusician userFollowingMusician = new UserFollowingMusician();
        userFollowingMusician.setId(mId);
        userFollowingMusician.setImage(mImage);
        userFollowingMusician.setName(mName);
        userFollowingMusician.setFollower_id(mUserId);

        mViewModel = ViewModelProviders.of(this).get(MusicianViewModel.class);
        mViewModel.init(userFollowingMusician);
        mViewModel.getMusician().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    updateUi(user);
                }
            }
        });

        mViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
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
        mViewModel.getIsFollowing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFollowing) {
                if (isFollowing != null) {
                    if (isFollowing) {
                        mFollowButton.setBackground(getResources().getDrawable(R.drawable.rounded_background));
                        mFollowButton.setText(getString(R.string.following));
                    } else {
                        mFollowButton.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
                        mFollowButton.setText(getString(R.string.follow));
                    }
                }
            }
        });
    }

    private void updateUi(User user) {
        Picasso.get().load(user.getImage()).into(new Target() {
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
        mFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.handleFollower();
            }
        });

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

        return view;
    }
}
