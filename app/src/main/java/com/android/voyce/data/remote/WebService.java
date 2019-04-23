package com.android.voyce.data.remote;

import com.android.voyce.data.model.Musician;
import com.android.voyce.data.model.Proposal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WebService {

    @GET("artists")
    Call<List<Musician>> getMusicians();

    @GET("artists/{id}")
    Call<Musician> getMusician(@Path("id") String id);

    @GET("proposal/{id}")
    Call<Proposal> getMusicianProposals(@Path("id") String id);

//    @GET("proposal/{id}")
//    Call<List<Proposal>> getMusicianProposals(@Path("id") String id);
}
