package com.android.voyce.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "user")
public class User {

    @NonNull
    @PrimaryKey
    private String id;
    private String signal_id;
    private String email;
    private String name;
    private String image;
    private String city;
    private String state;
    private long date_of_birth_timestamp;
    private int gender;
    private int type;
    private long sponsors;
    private long listeners;
    private long followers;
    private long following;
    private long sponsoring;
    private String facebook_url;
    private String instagram_url;
    private String twitter_url;
    private String phone_number;
    private String biography;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFacebook_url() {
        return facebook_url;
    }

    public void setFacebook_url(String facebook_url) {
        this.facebook_url = facebook_url;
    }

    public String getInstagram_url() {
        return instagram_url;
    }

    public void setInstagram_url(String instagram_url) {
        this.instagram_url = instagram_url;
    }

    public String getTwitter_url() {
        return twitter_url;
    }

    public void setTwitter_url(String twitter_url) {
        this.twitter_url = twitter_url;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public long getSponsors() {
        return sponsors;
    }

    public void setSponsors(long sponsors) {
        this.sponsors = sponsors;
    }

    public long getListeners() {
        return listeners;
    }

    public void setListeners(long listeners) {
        this.listeners = listeners;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSignal_id() {
        return signal_id;
    }

    public void setSignal_id(String signal_id) {
        this.signal_id = signal_id;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getSponsoring() {
        return sponsoring;
    }

    public void setSponsoring(long sponsoring) {
        this.sponsoring = sponsoring;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDate_of_birth_timestamp() {
        return date_of_birth_timestamp;
    }

    public void setDate_of_birth_timestamp(long date_of_birth_timestamp) {
        this.date_of_birth_timestamp = date_of_birth_timestamp;
    }
}
