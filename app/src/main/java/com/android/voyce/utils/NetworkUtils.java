package com.android.voyce.utils;

import com.android.voyce.models.Musician;
import com.android.voyce.models.MusicianMainInfo;

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
import java.util.Random;

public final class NetworkUtils {

    private NetworkUtils() {}

    public static ArrayList<Musician> fetchMusicianInfoData(String urlString) {
        URL url = createUrl(urlString);

        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Musician> musicianArrayList = new ArrayList<>();

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
            e.printStackTrace();
        }

        return musicianArrayList;
    }

    public static Musician fetchMusicianDetailsData(String urlString) {
        URL url = createUrl(urlString);

        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
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

            musician = new Musician(id, imageUrl, name, bioText, listeners, followers, sponsors, facebook, instagram, twitter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return musician;
    }

    private static URL createUrl(String urlString) {
        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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