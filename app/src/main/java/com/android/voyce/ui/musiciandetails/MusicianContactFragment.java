package com.android.voyce.ui.musiciandetails;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.data.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianContactFragment extends Fragment{

    private TextView mPhone;
    private TextView mFacebookUrl;
    private TextView mInstagramUrl;
    private TextView mTwitterUrl;

    private Linkify.TransformFilter mTransformFilter = new Linkify.TransformFilter() {
        public final String transformUrl(final Matcher match, String url) {
            return "";
        }
    };

    public MusicianContactFragment() {
        // Required empty public constructor
    }

    public static MusicianContactFragment newInstance() {
        return new MusicianContactFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MusicianViewModel viewModel = ViewModelProviders.of(getParentFragment()).get(MusicianViewModel.class);

        viewModel.getMusician().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    mPhone.setText(user.getPhone_number());

                    Linkify.addLinks(mInstagramUrl, Pattern.compile(getString(R.string.instagram)),
                            user.getInstagram_url(), null, mTransformFilter);
                    Linkify.addLinks(mFacebookUrl, Pattern.compile(getString(R.string.facebook)),
                            user.getFacebook_url(), null, mTransformFilter);
                    Linkify.addLinks(mTwitterUrl, Pattern.compile(getString(R.string.twitter)),
                            user.getTwitter_url(), null, mTransformFilter);

                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        mPhone = view.findViewById(R.id.musician_phone_number);
        mFacebookUrl = view.findViewById(R.id.musician_facebook_url);
        mInstagramUrl = view.findViewById(R.id.musician_instagram_url);
        mTwitterUrl = view.findViewById(R.id.musician_twitter_url);

        return view;
    }

}
