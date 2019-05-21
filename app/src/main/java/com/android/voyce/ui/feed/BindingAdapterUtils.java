package com.android.voyce.ui.feed;

import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.android.voyce.data.model.Post;
import com.squareup.picasso.Picasso;

public class BindingAdapterUtils {
    @BindingAdapter("userImage")
    public static void setUserImage(ImageView view, Post post) {
        Picasso.get().load(post.getUser_image()).into(view);
    }

    @BindingAdapter("image")
    public static void setImage(ImageView view, Post post) {
        if (post.getImage() != null) {
            view.setVisibility(View.VISIBLE);
            Picasso.get().load(post.getImage()).into(view);
        }
    }

    @BindingAdapter("userName")
    public static void setUserName(TextView view, Post post) {
        view.setText(post.getUser_name());
    }

    @BindingAdapter("text")
    public static void setText(TextView view, Post post) {
        view.setText(post.getText());
    }

    @BindingAdapter("time")
    public static void setTime(TextView view, Post post) {
        view.setText(formatDate(post.getTimestamp()));

    }

    private static String formatDate(long timestamp) {
        return (String) DateUtils.getRelativeTimeSpanString(timestamp,
                System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
    }
}
