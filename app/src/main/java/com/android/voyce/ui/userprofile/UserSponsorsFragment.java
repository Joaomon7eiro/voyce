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
import com.android.voyce.data.model.UserSponsoringProposal;
import com.android.voyce.databinding.FragmentUserSponsorsBinding;
import com.android.voyce.utils.ConnectivityHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSponsorsFragment extends Fragment implements ListItemClickListener {
    private UserSponsoringAdapter mAdapter;
    private FragmentUserSponsorsBinding mBinding;

    public UserSponsorsFragment() {
        // Required empty public constructor
    }

    public static UserSponsorsFragment newInstance() {
        UserSponsorsFragment fragment = new UserSponsorsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_sponsors, container, false);

        ((AppCompatActivity)getActivity()).setSupportActionBar(mBinding.toolbarSponsors);

        mBinding.toolbarSponsors.setNavigationIcon(R.drawable.ic_action_back);
        mBinding.toolbarSponsors.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(mBinding.getRoot()).popBackStack();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new UserSponsoringAdapter(this);

        mBinding.rvUserSponsoring.setLayoutManager(layoutManager);
        mBinding.rvUserSponsoring.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserProfileViewModel viewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        viewModel.init();
        viewModel.getUserSponsoringProposals().observe(this, new Observer<List<UserSponsoringProposal>>() {
            @Override
            public void onChanged(@Nullable List<UserSponsoringProposal> userSponsoringProposals) {
                if (userSponsoringProposals != null && userSponsoringProposals.size() > 0) {
                    mBinding.noSponsoring.setVisibility(View.GONE);
                    mBinding.sponsoringContainer.setVisibility(View.VISIBLE);
                    mAdapter.setData(userSponsoringProposals);
                } else {
                    mBinding.sponsoringContainer.setVisibility(View.GONE);
                    mBinding.noSponsoring.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onListItemClick(int index) {
        if (ConnectivityHelper.isConnected(getContext())) {
            UserSponsoringProposal proposal = mAdapter.getData().get(index);
            if (proposal != null) {
                UserSponsorsFragmentDirections.ActionUserSponsorsFragmentToMusicianFragment action =
                        UserSponsorsFragmentDirections.actionUserSponsorsFragmentToMusicianFragment(
                                proposal.getUser_id(),
                                proposal.getUser_name(), proposal.getUser_image(), true);

                Navigation.findNavController(mBinding.getRoot()).navigate(action);
            }
        } else {
            Snackbar.make(getView(), getContext().getResources().getString(R.string.verify_connection), Snackbar.LENGTH_LONG).show();
        }
    }
}
