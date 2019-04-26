package com.android.voyce.ui.musiciandetails;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.Musician;
import com.android.voyce.data.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianInfoFragment extends Fragment {

    private TextView mBiography;
    private TextView mMonthlyIncome;
    private TextView mTotalSponsors;
    private TextView mInstagram ;
    private TextView mFacebook;
    private TextView mTwitter;

    public MusicianInfoFragment() {
    }

    public static MusicianInfoFragment newInstance() {
        return new MusicianInfoFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MusicianViewModel viewModel = ViewModelProviders.of(getParentFragment()).get(MusicianViewModel.class);
        viewModel.getMusician().observe(getParentFragment(), new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mBiography.setText(user.getBiography());
                    String monthlyIncome = String.valueOf("0");

                    mMonthlyIncome.setText(getString(R.string.monthly_income_value, monthlyIncome));
                    mTotalSponsors.setText(String.valueOf(user.getSponsors()));

                    Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
                        public final String transformUrl(final Matcher match, String url) {
                            return "";
                        }
                    };
                    Linkify.addLinks(mInstagram, Pattern.compile(getString(R.string.instagram)), user.getInstagram_url(), null, transformFilter);
                    Linkify.addLinks(mFacebook, Pattern.compile(getString(R.string.facebook)), user.getFacebook_url(), null, transformFilter);
                    Linkify.addLinks(mTwitter, Pattern.compile(getString(R.string.twitter)), user.getTwitter_url(), null, transformFilter);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_musician_info, container, false);

        mBiography = view.findViewById(R.id.biography_text);
        mMonthlyIncome = view.findViewById(R.id.monthly_income);
        mTotalSponsors = view.findViewById(R.id.total_sponsors);
        mInstagram = view.findViewById(R.id.instagram_url);
        mFacebook = view.findViewById(R.id.facebook_url);
        mTwitter = view.findViewById(R.id.twitter_url);

        return view;
    }

}
