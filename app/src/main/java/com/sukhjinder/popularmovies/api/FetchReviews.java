package com.sukhjinder.popularmovies.api;

import android.net.Uri;
import android.os.AsyncTask;

import com.sukhjinder.popularmovies.BuildConfig;
import com.sukhjinder.popularmovies.adapter.ReviewAdapter;
import com.sukhjinder.popularmovies.model.Review;

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

public class FetchReviews extends AsyncTask<String, Void, ArrayList<Review>> {

    private ReviewAdapter reviewAdapter;

    public FetchReviews(ReviewAdapter reviewAdapter) {
        this.reviewAdapter = reviewAdapter;
    }

    @Override
    protected ArrayList<Review> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            final String BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String API_KEY = "api_key";
            final String REVIEWS = "reviews";
            final String GET = "GET";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath(REVIEWS)
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
            return getReviewDataFromJSON(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<Review> getReviewDataFromJSON(String jsonString) throws JSONException {

        final ArrayList<Review> reviews = new ArrayList<>();
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String RESULTS = "results";

        JSONObject reviewJson = new JSONObject(jsonString);
        JSONArray resultsArray = reviewJson.getJSONArray(RESULTS);

        for (int i = 0; i < resultsArray.length(); i++) {
            String author;
            String content;

            JSONObject reviewInfo = resultsArray.getJSONObject(i);
            author = reviewInfo.getString(AUTHOR);
            content = reviewInfo.getString(CONTENT);

            Review review = new Review();
            review.setAuthor(author);
            review.setContent(content);

            reviews.add(review);
        }
        return reviews;
    }


    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {
        if (reviews != null) {
            for (Review review : reviews) {
                reviewAdapter.addReview(review);
            }
            reviewAdapter.notifyDataSetChanged();
        }
    }
}