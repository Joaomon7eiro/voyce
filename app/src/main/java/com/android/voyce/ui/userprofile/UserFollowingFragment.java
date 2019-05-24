package com.android.voyce.ui.userprofile;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.databinding.FragmentUserFollowingBinding;
import com.android.voyce.utils.ConnectivityHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowingFragment extends Fragment implements ListItemClickListener {

    private UserFollowingAdapter mAdapter;
    private FragmentUserFollowingBinding mBinding;

    public UserFollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_following, container, false);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.toolbarFollowing);

        mBinding.toolbarFollowing.setNavigationIcon(R.drawable.ic_action_back);
        mBinding.toolbarFollowing.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(mBinding.getRoot()).popBackStack();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new UserFollowingAdapter(this);

        mBinding.rvUserFollowing.setLayoutManager(layoutManager);
        mBinding.rvUserFollowing.setAdapter(mAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onListItemClick(int index) {
        if (ConnectivityHelper.isConnected(getContext())) {
            UserFollowingMusician user = mAdapter.getData().get(index);
            if (user != null) {
                UserFollowingFragmentDirections.ActionUserFollowingFragmentToMusicianFragment action =
                        UserFollowingFragmentDirections.actionUserFollowingFragmentToMusicianFragment(
                                user.getId(), user.getName(), user.getImage(), false);

                Navigation.findNavController(mBinding.getRoot()).navigate(action);
            }
        } else {
            Snackbar.make(getView(), getContext().getResources().getString(R.string.verify_connection), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserProfileViewModel viewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        viewModel.init();
        viewModel.getUserFollowingMusicians().observe(this, new Observer<List<UserFollowingMusician>>() {
            @Override
            public void onChanged(@Nullable List<UserFollowingMusician> userFollowingMusicians) {
                if (userFollowingMusicians != null && userFollowingMusicians.size() < 1) {
                    mBinding.followingContainer.setVisibility(View.GONE);
                    mBinding.noFollowing.setVisibility(View.VISIBLE);
                } else {
                    mBinding.followingContainer.setVisibility(View.VISIBLE);
                    mBinding.noFollowing.setVisibility(View.GONE);
                    mAdapter.setData(userFollowingMusicians);
                }
            }
        });
    }
}
