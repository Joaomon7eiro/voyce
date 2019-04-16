package com.android.voyce.utils;

import com.android.voyce.models.Musician;
import com.android.voyce.models.SearchMusicianInfo;

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

public class NetworkUtils {

    private NetworkUtils() {}

    public static ArrayList<SearchMusicianInfo> fetchMusicianInfoData(String urlString) {
        URL url = createUrl(urlString);

        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<SearchMusicianInfo> musicianArrayList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject artistsObject = jsonObject.getJSONObject("artists");
            JSONArray artists = artistsObject.getJSONArray("artist");

            for (int i=0; i < artists.length(); i++) {
                JSONObject artist = artists.getJSONObject(i);
                String name = artist.getString("name");
                String playcount = artist.getString("playcount");
                String listeners = artist.getString("listeners");

                JSONArray images = artist.getJSONArray("image");
                String imageUrl = images.getJSONObject(4).getString("#text");

                Random random = new Random();
                int randInt = random.nextInt(40000);

                SearchMusicianInfo musician = new SearchMusicianInfo(imageUrl, name, playcount,
                        listeners, String.valueOf(randInt));

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
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject artist = jsonObject.getJSONObject("artist");

            JSONObject bio = artist.getJSONObject("bio");
            String bioText = bio.getString("summary");

            musician = new Musician(1, "a", bioText, 1, 1, 1);
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