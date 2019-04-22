package com.android.voyce.utils;

import android.util.Log;

import com.android.voyce.data.models.Musician;
import com.android.voyce.data.models.Proposal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static final String API_BASE_URL = "https://5cb65ce3a3763800149fc8fd.mockapi.io/api/";

    private NetworkUtils() {}

    public static ArrayList<Musician> fetchMusicianMainInfoData(String urlString) {
        URL url = createUrl(urlString);
        ArrayList<Musician> musicianArrayList = new ArrayList<>();

        if (url == null) {
            return musicianArrayList;
        }

        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "fetchMusicianMainInfoData: ", e);
        }

        try {
            JSONArray artists = new JSONArray(jsonResponse);

            for (int i=0; i < artists.length(); i++) {
                JSONObject artist = artists.getJSONObject(i);
                String id = artist.getString("id");
                String name = artist.getString("name");
                int listeners = artist.getInt("total_listeners");
                int sponsors = artist.getInt("total_sponsors");
                int followers = artist.getInt("total_followers");
                String imageUrl = artist.getString("image");

                Musician musician = new Musician(id, imageUrl, name, listeners,
                    followers, sponsors);

                musicianArrayList.add(musician);
            }
        } catch (JSONException e) {
            Log.e(TAG, "fetchMusicianMainInfoData: ", e);
        }

        return musicianArrayList;
    }

    public static Musician fetchMusicianDetailsData(String urlString) {
        URL url = createUrl(urlString);

        if (url == null) {
            return null;
        }

        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "fetchMusicianDetailsData: ", e);
        }

        Musician musician = null;

        try {
            JSONObject artist = new JSONObject(jsonResponse);

            String id = artist.getString("id");
            String name = artist.getString("name");
            String bioText = artist.getString("biography");
            String imageUrl = artist.getString("image");
            int listeners = artist.getInt("total_listeners");
            int sponsors = artist.getInt("total_sponsors");
            int followers = artist.getInt("total_followers");
            String instagram = artist.getString("instagram_url");
            String facebook = artist.getString("facebook_url");
            String twitter = artist.getString("twitter_url");
            double monthlyIncome = artist.getDouble("total_monthly_income");

            musician = new Musician(id, imageUrl, name, bioText, listeners, followers, sponsors,
                    facebook, instagram, twitter, monthlyIncome);

        } catch (JSONException e) {
            Log.e(TAG, "fetchMusicianDetailsData: ", e);
        }

        return musician;
    }

    public static ArrayList<Proposal> fetchMusicianProposalsData(String urlString) {
        URL url = createUrl(urlString);
        ArrayList<Proposal> proposalArrayList = new ArrayList<>();

        if (url == null) {
            return proposalArrayList;
        }

        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "fetchMusicianProposalsData: ", e);
        }



        try {
            JSONObject proposal = new JSONObject(jsonResponse);

            String name = proposal.getString("name");
            String imageUrl = proposal.getString("image");
            String description = proposal.getString("description");
            double price = proposal.getDouble("price");

            proposalArrayList.add(new Proposal(name, imageUrl, description, price));
        } catch (JSONException e) {
            Log.e(TAG, "fetchMusicianProposalsData: ", e);
        }

        return proposalArrayList;
    }

    private static URL createUrl(String urlString) {
        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(TAG, "createUrl: ", e);
        }

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String jsonResponse = "";
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readInputStream(inputStream);
            }

        } catch (IOException e) {
            Log.e(TAG, "makeHttpRequest: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder output = new StringBuilder();
        String line = bufferedReader.readLine();

        while (line != null) {
            output.append(line);
            line = bufferedReader.readLine();
        }
        return output.toString();
    }
}