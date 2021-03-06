package com.android.voyce.ui.musiciandetails;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.LayoutTransition;
import android.app.AlertDialog;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.Post;
import com.android.voyce.data.model.Song;
import com.android.voyce.databinding.FeedListItemBinding;
import com.android.voyce.databinding.FragmentMusicianBinding;
import com.android.voyce.databinding.ProposalDialogBinding;
import com.android.voyce.ui.adapter.PopularSongsAdapter;
import com.android.voyce.ui.adapter.ProposalsAdapter;
import com.android.voyce.ui.main.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.android.voyce.R;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianFragment extends Fragment implements ListItemClickListener, PopularSongsAdapter.SongListItemClick {

    private String mMusicianId;
    private String mMusicianName;
    private String mMusicianImage;

    private MusicianViewModel mViewModel;

    private String mUserId;
    private String mUserName;
    private String mUserImage;

    private String mSignalId;
    private FragmentMusicianBinding mBinding;

    private ProposalsAdapter mAdapter;
    private PopularSongsAdapter mSongsAdapter;

    private boolean mScrollToPlans = false;
    private Handler mScrollHandler;
    private static final int SCROLL_DELAY = 1000;
    private static final int OFFSET = 150;

    private Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int scrollTo = mBinding.goalCard.getTop() + OFFSET;
            mBinding.containerMusicianScroll.smoothScrollTo(0, scrollTo);
            mScrollHandler.removeCallbacks(mScrollRunnable);
        }
    };

    private View.OnClickListener mBackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Navigation.findNavController(mBinding.getRoot()).popBackStack();
        }
    };
    private String mPosition;

    public MusicianFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();

        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));

        MusicianFragmentArgs args = MusicianFragmentArgs.fromBundle(getArguments());
        mMusicianId = args.getId();
        mMusicianName = args.getName();
        mMusicianImage = args.getImage();
        mPosition = args.getPosition();
        mScrollToPlans = args.getScrollToPlans();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserId = sharedPreferences.getString(Constants.KEY_CURRENT_USER_ID, null);
        mUserName = sharedPreferences.getString(Constants.KEY_CURRENT_USER_NAME, null);
        mUserImage = sharedPreferences.getString(Constants.KEY_CURRENT_USER_IMAGE, null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentMusicianBinding.inflate(inflater);

        Animator animator = AnimatorInflater.loadAnimator(getContext(), R.animator.pop_up);

        LayoutTransition transition = new LayoutTransition();
        transition.setAnimator(LayoutTransition.APPEARING, animator);
        transition.setStartDelay(LayoutTransition.APPEARING, 0);
        mBinding.containerMusician.setLayoutTransition(transition);

        mBinding.musicianBackButton.setOnClickListener(mBackOnClickListener);

        mBinding.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.handleFollower(mSignalId, mUserName);
            }
        });

        String[] tabsTitle = new String[]{"Info", "Contato"};
        MusicianFragmentPagerAdapter mPagerAdapter = new MusicianFragmentPagerAdapter(getChildFragmentManager(), tabsTitle);
        mBinding.musicianViewPager.setAdapter(mPagerAdapter);
        mBinding.musicianTabLayout.setupWithViewPager(mBinding.musicianViewPager);

        mSongsAdapter = new PopularSongsAdapter(this);
        mBinding.popularSongsRv.setAdapter(mSongsAdapter);
        mBinding.popularSongsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.popularSongsRv.setHasFixedSize(true);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.rvMusicianProposals.setLayoutManager(layoutManager);

        mAdapter = new ProposalsAdapter(this);
        mBinding.rvMusicianProposals.setAdapter(mAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mBinding.rvMusicianProposals);

        setupFeedRecyclerView();

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.musicianProfileImage.setTransitionName(mPosition);
        Glide.with(this).load(mMusicianImage).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                startPostponedEnterTransition();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                startPostponedEnterTransition();
                return false;
            }
        }).into(mBinding.musicianProfileImage);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserFollowingMusician userFollowingMusician = new UserFollowingMusician();
        userFollowingMusician.setId(mMusicianId);
        userFollowingMusician.setImage(mMusicianImage);
        userFollowingMusician.setName(mMusicianName);
        userFollowingMusician.setFollower_id(mUserId);
        userFollowingMusician.setFollower_name(mUserName);
        userFollowingMusician.setFollower_image(mUserImage);

        mViewModel = ViewModelProviders.of(this).get(MusicianViewModel.class);
        mViewModel.init(userFollowingMusician);
        mViewModel.getMusician().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mBinding.setMusician(user);
                    mBinding.musicianLocation.setVisibility(View.VISIBLE);
                    mSignalId = user.getSignal_id();
                }
            }
        });

        mViewModel.getGoal().observe(this, new Observer<Goal>() {
            @Override
            public void onChanged(@Nullable Goal goal) {
                if (goal != null) {
                    mBinding.setGoal(goal);
                }
            }
        });

        mViewModel.getProposals().observe(this, new Observer<List<Proposal>>() {
            @Override
            public void onChanged(@Nullable List<Proposal> proposals) {
                if (proposals != null && proposals.size() > 0) {
                    mAdapter.setData(proposals);
                    mBinding.setProposalsSize(proposals.size());
                }
            }
        });

        mViewModel.getPopularSongs().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                if (songs != null && songs.size() > 0) {
                    mSongsAdapter.setData(songs);
                    mBinding.setSongsSize(songs.size());
                }
            }
        });

        mViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                if (isLoading != null) {
                    mBinding.setIsLoading(isLoading);
                    if (!isLoading) {
                        if (mScrollToPlans) {
                            mScrollHandler = new Handler();
                            mScrollHandler.postDelayed(mScrollRunnable, SCROLL_DELAY);
                        }
                    }
                }
            }
        });

        mViewModel.getIsFollowing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isFollowing) {
                if (isFollowing != null) {
                    mBinding.setIsFollowing(isFollowing);
                    if (isFollowing) {
                        mBinding.followButton.setText(getString(R.string.following));
                    } else {
                        mBinding.followButton.setText(getString(R.string.follow));
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(int index) {
        final Proposal proposal = mAdapter.getData().get(index);
        if (proposal != null) {
            LayoutInflater layoutInflater = ((AppCompatActivity) getContext()).getLayoutInflater();
            final ProposalDialogBinding binding = DataBindingUtil
                    .inflate(layoutInflater, R.layout.proposal_dialog, null, false);

            binding.setProposal(proposal);

            mViewModel.getIsSponsoring(proposal.getId()).observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isSponsoring) {
                    if (isSponsoring != null && isSponsoring) {
                        binding.sponsorButton.setText(getResources().getString(R.string.cancel_plan));
                    } else {
                        binding.sponsorButton.setText(getResources().getString(R.string.sponsor_artist));
                    }
                }
            });
            mViewModel.getProposalLoading().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean isLoading) {
                    if (isLoading != null && isLoading) {
                        binding.proposalLoading.setVisibility(View.VISIBLE);
                        binding.sponsorButton.setVisibility(View.INVISIBLE);
                    } else {
                        binding.proposalLoading.setVisibility(View.GONE);
                        binding.sponsorButton.setVisibility(View.VISIBLE);
                    }
                }
            });

            binding.sponsorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.handleSponsor(mSignalId, mUserName, proposal);
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(binding.getRoot());
            builder.show();
        }
    }

    private void setupFeedRecyclerView() {
        Query baseQuery = FirebaseFirestore.getInstance()
                .collection("user_posts")
                .document(mMusicianId)
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);


        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        FirestorePagingOptions<Post> options = new FirestorePagingOptions.Builder<Post>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, Post.class)
                .build();

        FirestorePagingAdapter<Post, ViewHolder> adapter =
                new FirestorePagingAdapter<Post, ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Post post) {
                        viewHolder.bindTo(post);
                    }

                    @NonNull
                    @Override
                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        FeedListItemBinding binding = DataBindingUtil
                                .inflate(LayoutInflater.from(getContext()),
                                        R.layout.feed_list_item, parent,
                                        false);
                        return new ViewHolder(binding);
                    }
                };

        mBinding.musicianFeedRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.musicianFeedRv.setAdapter(adapter);
        mBinding.musicianFeedRv.setNestedScrollingEnabled(false);
    }

    @Override
    public void onSongListItemClick(int index) {
        Song song = mSongsAdapter.getData().get(index);
        if (song != null) {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                activity.startPlayerService();
                activity.playSingles(mSongsAdapter.getData(), song.getId());
            }
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private FeedListItemBinding mBinding;

        ViewHolder(@NonNull FeedListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bindTo(Post post) {
            mBinding.setPost(post);
            mBinding.executePendingBindings();
        }
    }
}
