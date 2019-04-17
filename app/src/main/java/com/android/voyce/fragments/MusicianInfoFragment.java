package com.android.voyce.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.models.Musician;
import com.android.voyce.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianInfoFragment extends Fragment {

    private Musician mMusician;
    private String mBioText = "";
    private String mMonthlyIncome = "";
    private String mTotalSponsors = "";
    private String mInstagramUrl = "";
    private String mFacebookUrl = "";
    private String mTwitterUrl = "";

    public MusicianInfoFragment() {
    }

    public static MusicianInfoFragment newInstance(Musician musician) {
        MusicianInfoFragment fragment = new MusicianInfoFragment();

        Bundle args = new Bundle();
        args.putSerializable(Constants.KEY_MUSICIAN, musician);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMusician = (Musician) getArguments().getSerializable(Constants.KEY_MUSICIAN);
        }

        if (mMusician != null) {
            mBioText = mMusician.getBiography();
            mInstagramUrl = mMusician.getInstagramUrl();
            mFacebookUrl = mMusician.getFacebookUrl();
            mTwitterUrl = mMusician.getTwitterUrl();
            mMonthlyIncome = String.valueOf(mMusician.getMonthlyIncome());
            mTotalSponsors = String.valueOf(mMusician.getSponsorsNumber());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_musician_info, container, false);

        TextView biography = view.findViewById(R.id.biography_text);
        biography.setText(mBioText);

        TextView monthlyIncome = view.findViewById(R.id.monthly_income);
        monthlyIncome.setText(getString(R.string.monthly_income_value, mMonthlyIncome));

        TextView totalSponsors = view.findViewById(R.id.total_sponsors);
        totalSponsors.setText(mTotalSponsors);

        TextView instagram = view.findViewById(R.id.instagram_url);
        TextView facebook = view.findViewById(R.id.facebook_url);
        TextView twitter = view.findViewById(R.id.twitter_url);

        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return "";
            } };

        Linkify.addLinks(instagram, Pattern.compile(getString(R.string.instagram)), mInstagramUrl, null, transformFilter);
        Linkify.addLinks(facebook, Pattern.compile(getString(R.string.facebook)), mFacebookUrl, null, transformFilter);
        Linkify.addLinks(twitter, Pattern.compile(getString(R.string.twitter)), mTwitterUrl, null, transformFilter);

        return view;
    }

}
