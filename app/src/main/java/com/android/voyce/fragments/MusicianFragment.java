package com.android.voyce.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.SimpleFragmentPagerAdapter;
import com.jgabrielfreitas.core.BlurImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicianFragment extends Fragment {


    public MusicianFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle args = this.getArguments();

        int imageId = args.getInt("image_resource_id");
        String name = args.getString("name");

        View view = inflater.inflate(R.layout.fragment_musician, container, false);

        ViewPager viewPager = view.findViewById(R.id.view_pager);

        SimpleFragmentPagerAdapter pagerAdapter = new SimpleFragmentPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        BlurImageView backgroundImage = view.findViewById(R.id.musician_background_image);
        ImageView profileImage = view.findViewById(R.id.musician_profile_image);
        TextView musicianName = view.findViewById(R.id.musician_name);

        profileImage.setImageResource(imageId);
        backgroundImage.setImageResource(imageId);
        musicianName.setText(name);

        backgroundImage.setBlur(2);

        return view;
    }

}
