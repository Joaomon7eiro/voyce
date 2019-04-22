package com.android.voyce.ui.musiciandetails;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.android.voyce.data.database.AppDatabase;
import com.android.voyce.data.database.AppExecutors;
import com.android.voyce.data.models.MusicianModel;
import com.android.voyce.data.loaders.MusicianFragmentLoader;
import com.android.voyce.data.models.Musician;
import com.android.voyce.data.models.MusicianAndProposals;
import com.android.voyce.utils.Constants;
import com.android.voyce.utils.NetworkUtils;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.concurrent.Executor;

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

    private MusicianModel mMusicianModel;
    private Bitmap mBitMapImage;
    private String mName;

    private MusicianFragmentPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private AppDatabase mDb;

    private static final int LOADER_ID = 2;

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
                        if (mMusicianModel == null) {
                            mMusicianModel = new MusicianModel(mId, mName, mBitMapImage);
                        }
                        mDb.musicianDao().insertMusician(mMusicianModel);
                    }
                });
            } else {
                mFollowButton.setBackground(getResources().getDrawable(R.drawable.transparent_bg_bordered));
                mFollowButton.setText(getString(R.string.follow));
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.musicianDao().deleteMusician(mMusicianModel);
                    }
                });
            }
        }
    };

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            mId = args.getString(Constants.KEY_MUSICIAN_ID);
        }

        mTabsTitle = new String[]{getString(R.string.info_tab), getString(R.string.proposal_tab)};
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
        mTabLayout = view.findViewById(R.id.tab_layout);

        mBackgroundImage = view.findViewById(R.id.musician_background_image);
        mProfileImage = view.findViewById(R.id.musician_profile_image);
        mMusicianName = view.findViewById(R.id.musician_name);

        mDb = AppDatabase.getInstance(getContext());

        LiveData<MusicianModel> musician = mDb.musicianDao().queryMusiciansById(mId);
        musician.observe(this, new Observer<MusicianModel>() {
            @Override
            public void onChanged(@Nullable MusicianModel musicianModel) {
                mMusicianModel = musicianModel;
                if (mMusicianModel != null) {
                    isFollowing = true;
                    mFollowButton.setBackground(getResources().getDrawable(R.drawable.rounded_background));
                    mFollowButton.setText(getString(R.string.following));
                }
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Loader<MusicianAndProposals> onCreateLoader(int i, @Nullable Bundle bundle) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAppBarLayout.setVisibility(View.GONE);

        String musicianBaseUrl = NetworkUtils.API_BASE_URL + "artists/" + mId;

        Uri musicianUrl = Uri.parse(musicianBaseUrl).buildUpon()
                .build();

        String proposalsBaseUrl = NetworkUtils.API_BASE_URL + "proposal/" + mId;
        Uri proposalsUrl = Uri.parse(proposalsBaseUrl).buildUpon()
                .build();

        return new MusicianFragmentLoader(getContext(), musicianUrl.toString(), proposalsUrl.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<MusicianAndProposals> loader, MusicianAndProposals musicianAndProposals) {
        mProgressBar.setVisibility(View.GONE);
        mAppBarLayout.setVisibility(View.VISIBLE);

        mPagerAdapter = new MusicianFragmentPagerAdapter(getChildFragmentManager(), mTabsTitle, musicianAndProposals);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        Musician musician = musicianAndProposals.getMusician();
        if (musician != null) {
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
    }

    @Override
    public void onLoaderReset(@NonNull Loader<MusicianAndProposals> loader) {
        mPagerAdapter = null;
    }
}
