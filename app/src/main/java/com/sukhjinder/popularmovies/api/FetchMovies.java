package com.sukhjinder.popularmovies.api;

import android.net.Uri;
import android.os.AsyncTask;

import com.sukhjinder.popularmovies.BuildConfig;
import com.sukhjinder.popularmovies.adapter.MovieAdapter;
import com.sukhjinder.popularmovies.model.Movie;

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

public class FetchMovies extends AsyncTask<String, Void, ArrayList<Movie>> {

    private MovieAdapter movieAdapter;

    public FetchMovies(MovieAdapter movieAdapter) {
        this.movieAdapter = movieAdapter;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            final String BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(API_KEY, BuildConfig.TMDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
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
            return getMovieDataFromJSON(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<Movie> getMovieDataFromJSON(String jsonString) throws JSONException {

        final ArrayList<Movie> movies = new ArrayList<>();

        final String RESULTS = "results";
        final String ID = "id";
        final String TITLE = "title";
        final String POSTER_PATH = "poster_path";
        final String BACKDROP_PATH = "backdrop_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVERAGE = "vote_average";

        JSONObject movieJson = new JSONObject(jsonString);
        JSONArray resultsArray = movieJson.getJSONArray(RESULTS);

        for (int i = 0; i < resultsArray.length(); i++) {
            int id;
            String title;
            String poster;
            String backdrop;
            String plotSynopsis;
            String releaseDate;
            double userRating;


            JSONObject movieInfo = resultsArray.getJSONObject(i);

            id = movieInfo.getInt(ID);
            title = movieInfo.getString(TITLE);
            poster = movieInfo.getString(POSTER_PATH);
            backdrop = movieInfo.getString(BACKDROP_PATH);
            plotSynopsis = movieInfo.getString(OVERVIEW);
            releaseDate = movieInfo.getString(RELEASE_DATE);
            userRating = movieInfo.getDouble(VOTE_AVERAGE);


            Movie movie = new Movie();
            movie.setId(id);
            movie.setTitle(title);
            movie.setPoster(poster);
            movie.setBackdrop(backdrop);
            movie.setPlotSynopsis(plotSynopsis);
            movie.setReleaseDate(releaseDate);
            movie.setUserRating(userRating);

            movies.add(movie);
        }

        return movies;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        if (movies != null) {
            for (Movie movie : movies) {
                movieAdapter.addMovie(movie);
            }
            movieAdapter.notifyDataSetChanged();
        }
    }


}