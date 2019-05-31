package com.android.voyce.ui.usermusicianprofile;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.R;
import com.android.voyce.common.ListItemClickListener;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.databinding.FragmentUserFollowersBinding;
import com.android.voyce.ui.userprofile.UserFollowingAdapter;
import com.android.voyce.utils.ConnectivityHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFollowersFragment extends Fragment implements ListItemClickListener {


    private UserMusicianProfileViewModel mViewModel;
    private UserFollowingAdapter mAdapter;
    private FragmentUserFollowersBinding mBinding;

    public UserFollowersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentUserFollowersBinding.inflate(inflater);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.followersToolbar);

        mBinding.followersToolbar.setNavigationIcon(R.drawable.ic_action_back);
        mBinding.followersToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(mBinding.getRoot()).popBackStack();
            }
        });

        mAdapter = new UserFollowingAdapter(this);
        mBinding.followersRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.followersRv.setAdapter(mAdapter);
        mBinding.followersRv.setHasFixedSize(true);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UserMusicianProfileViewModel.class);
        mViewModel.initFollowers(FirebaseAuth.getInstance().getUid());
        mViewModel.getFollowers().observe(getViewLifecycleOwner(), new Observer<List<UserFollowingMusician>>() {
            @Override
            public void onChanged(List<UserFollowingMusician> userFollowingMusicians) {
                mAdapter.setData(userFollowingMusicians);
            }
        });
    }

    @Override
    public void onListItemClick(int index) {
        if (ConnectivityHelper.isConnected(getContext())) {
            UserFollowingMusician user = mAdapter.getData().get(index);
            if (user != null) {
                UserFollowersFragmentDirections.ActionUserFollowersFragmentToMusicianFragment action =
                        UserFollowersFragmentDirections.actionUserFollowersFragmentToMusicianFragment(
                                user.getId(),
                                user.getName(),
                                user.getImage(),
                                false,
                                String.valueOf(index));

                Navigation.findNavController(mBinding.getRoot()).navigate(action);
            }
        } else {
            Snackbar.make(getView(), getContext().getResources().getString(R.string.verify_connection), Snackbar.LENGTH_LONG).show();
        }
    }
}
