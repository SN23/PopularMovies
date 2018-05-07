package com.sukhjinder.popularmovies.api;

import android.net.Uri;
import android.os.AsyncTask;

import com.sukhjinder.popularmovies.BuildConfig;
import com.sukhjinder.popularmovies.adapter.TrailerAdapter;
import com.sukhjinder.popularmovies.model.Movie;
import com.sukhjinder.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class FetchTrailers extends AsyncTask<String, Void, ArrayList<Trailer>> {

    private TrailerAdapter trailerAdapter;

    public FetchTrailers(TrailerAdapter trailerAdapter) {
        this.trailerAdapter = trailerAdapter;
    }

    @Override
    protected ArrayList<Trailer> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            final String BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String API_KEY = "api_key";
            final String VIDEOS = "videos";
            final String GET = "GET";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(VIDEOS)
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            jsonString = buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            return getTrailerDataFromJSON(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<Trailer> getTrailerDataFromJSON(String jsonString) throws JSONException {

        final ArrayList<Trailer> trailers = new ArrayList<>();
        final String NAME = "name";
        final String KEY = "key";
        final String RESULTS = "results";

        JSONObject trailerJson = new JSONObject(jsonString);
        JSONArray resultsArray = trailerJson.getJSONArray(RESULTS);

        for (int i = 0; i < resultsArray.length(); i++) {
            String name;
            String key;

            JSONObject trailerInfo = resultsArray.getJSONObject(i);

            name = trailerInfo.getString(NAME);
            key = trailerInfo.getString(KEY);

            Trailer trailer = new Trailer();
            trailer.setName(name);
            trailer.setKey(key);

            trailers.add(trailer);
        }
        return trailers;
    }

    @Override
    protected void onPostExecute(ArrayList<Trailer> trailers) {
        if (trailers != null) {
            for (Trailer trailer : trailers) {
                trailerAdapter.addTrailer(trailer);
            }
            trailerAdapter.notifyDataSetChanged();
        }
    }
}
