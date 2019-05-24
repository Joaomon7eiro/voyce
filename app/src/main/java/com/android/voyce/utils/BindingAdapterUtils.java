package com.android.voyce.utils;

import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.android.voyce.R;
import com.android.voyce.data.model.Post;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.Song;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.android.voyce.data.model.UserSponsoringProposal;
import com.bumptech.glide.Glide;


public class BindingAdapterUtils {

    // FEED ADAPTER
    @BindingAdapter("userImage")
    public static void setUserImage(ImageView view, Post post) {
        Glide.with(view.getContext())
                .load(post.getUser_image())
                .placeholder(R.drawable.profile_placeholder)
                .thumbnail(0.4f)
                .into(view);
    }

    @BindingAdapter("postImage")
    public static void setPostImage(ImageView view, Post post) {
        if (post.getImage() != null) {
            view.setVisibility(View.VISIBLE);
            Glide.with(view.getContext())
                    .load(post.getImage())
                    .thumbnail(0.4f)
                    .into(view);
        }
    }

    @BindingAdapter("userName")
    public static void setUserName(TextView view, Post post) {
        view.setText(StringUtils.capitalize(post.getUser_name()));
    }

    @BindingAdapter("postText")
    public static void setPostText(TextView view, Post post) {
        view.setText(post.getText());
    }

    @BindingAdapter("postTime")
    public static void setPostTime(TextView view, Post post) {
        view.setText(formatDate(post.getTimestamp()));
    }

    private static String formatDate(long timestamp) {
        return (String) DateUtils.getRelativeTimeSpanString(timestamp,
                System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
    }

    // PROPOSAL ADAPTER
    @BindingAdapter("proposalName")
    public static void setProposalName(TextView view, Proposal proposal) {
        view.setText(proposal.getName());
    }

    @BindingAdapter("proposalPrice")
    public static void setProposalPrice(TextView view, Proposal proposal) {
        view.setText(String.valueOf(proposal.getPrice()));
    }

    // MUSICIAN ADAPTER
    @BindingAdapter("musicianName")
    public static void setMusicianName(TextView view, User musician) {
        view.setText(StringUtils.capitalize(musician.getName()));
    }

    @BindingAdapter("musicianImage")
    public static void setMusicianImage(ImageView view, User musician) {
        Glide.with(view.getContext())
                .load(musician.getImage())
                .thumbnail(0.4f)
                .into(view);
    }

    @BindingAdapter("musicianFollowersNumber")
    public static void setMusicianFollowersNumber(TextView view, User musician) {
        view.setText(formatNumber(String.valueOf(musician.getFollowers())));
    }

    @BindingAdapter("musicianSponsorsNumber")
    public static void setMusicianSponsorsNumber(TextView view, User musician) {
        view.setText(formatNumber(String.valueOf(musician.getSponsors())));
    }

    @BindingAdapter("musicianListenersNumber")
    public static void setMusicianListenersNumber(TextView view, User musician) {
        view.setText(formatNumber(String.valueOf(musician.getListeners())));
    }

    private static String formatNumber(String numberText) {
        int number = Integer.parseInt(numberText);
        String numberString;
        if (Math.abs(number / 1000000) >= 1) {
            numberString = number / 1000000 + "M";
        } else if (Math.abs(number / 1000) >= 1) {
            numberString = number / 1000 + "K";
        } else {
            numberString = String.valueOf(number);
        }
        return numberString;
    }

    // USER FOLLOWING ADAPTER
    @BindingAdapter("followingMusicianImage")
    public static void setFollowingMusicianImage(ImageView view, UserFollowingMusician musician) {
        Glide.with(view.getContext())
                .load(musician.getImage())
                .placeholder(R.drawable.profile_placeholder)
                .thumbnail(0.4f)
                .into(view);
    }

    @BindingAdapter("followingMusicianName")
    public static void setFollowingMusicianName(TextView view, UserFollowingMusician musician) {
        view.setText(StringUtils.capitalize(musician.getName()));
    }

    // USER SPONSORING ADAPTER
    @BindingAdapter("sponsoringMusicianImage")
    public static void setFollowingMusicianImage(ImageView view, UserSponsoringProposal sponsoringProposal) {
        Glide.with(view.getContext())
                .load(sponsoringProposal.getUser_image())
                .placeholder(R.drawable.profile_placeholder)
                .thumbnail(0.4f)
                .into(view);
    }

    @BindingAdapter("sponsoringMusicianName")
    public static void setSponsoringMusicianName(TextView view, UserSponsoringProposal sponsoringProposal) {
        view.setText(StringUtils.capitalize(sponsoringProposal.getUser_name()));
    }

    @BindingAdapter("sponsoringProposalName")
    public static void setSponsoringProposalName(TextView view, UserSponsoringProposal sponsoringProposal) {
        view.setText(sponsoringProposal.getName());
    }

    // POPULAR SONGS ADAPTER
    @BindingAdapter("popularSongTitle")
    public static void setPopularSongTitle(TextView view, Song song) {
        view.setText(song.getTitle());
    }

    @BindingAdapter("popularSongListeners")
    public static void setPopularSongListeners(TextView view, Song song) {
        view.setText("24433");
    }

    @BindingAdapter("popularSongTime")
    public static void setPopularSongTime(TextView view, Song song) {
        view.setText("3:35");
    }
}
