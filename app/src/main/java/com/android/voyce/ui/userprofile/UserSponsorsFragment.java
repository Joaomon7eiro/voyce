package com.android.voyce.ui.userprofile;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.android.voyce.utils.ConnectivityHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSponsorsFragment extends Fragment implements ListItemClickListener {

    private TextView mNoSponsoring;
    private LinearLayout mContainer;
    private UserSponsoringAdapter mAdapter;
    private View mRootView;

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
        mRootView = inflater.inflate(R.layout.fragment_user_sponsors, container, false);

        Toolbar toolbar = mRootView.findViewById(R.id.toolbar_sponsors);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(mRootView).popBackStack();
            }
        });

        mNoSponsoring = mRootView.findViewById(R.id.no_sponsoring);
        mContainer = mRootView.findViewById(R.id.sponsoring_container);

        RecyclerView recyclerView = mRootView.findViewById(R.id.rv_user_sponsoring);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new UserSponsoringAdapter(this);
        recyclerView.setAdapter(mAdapter);

        return mRootView;
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
                    mNoSponsoring.setVisibility(View.GONE);
                    mContainer.setVisibility(View.VISIBLE);
                    mAdapter.setData(userSponsoringProposals);
                } else {
                    mContainer.setVisibility(View.GONE);
                    mNoSponsoring.setVisibility(View.VISIBLE);
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

                Navigation.findNavController(mRootView).navigate(action);
            }
        } else {
            Snackbar.make(getView(), getContext().getResources().getString(R.string.verify_connection), Snackbar.LENGTH_LONG).show();
        }
    }
}
