package com.github.robroseknows.movieapp;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainFragment extends Fragment {

    private final String API_KEY = "hunter2";  // TODO: Remove before commit!!
    private final String LOG_TAG = "MainFragment";

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private class MovieObject {
        private int movieId = 286217;
        private String movieTitle = "The Martian";
        private String movieDescription = "";
        private String moviePosterPath = "/AjbENYG3b8lhYSkdrWwlhVLRPKR.jpg";
        private double movieVoteAvg = 7.7;
    }

    public class FetchMovieListTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            if(params.length == 0) {
                return null;
            }

            String[] out = new String[5];
            String movieListRawJson = "";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String CALL_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORTBY_PARAM = "sort_by";
                final String API_PARAM = "api_key";


                Uri builtUri = Uri.parse(CALL_BASE_URL).buildUpon()
                        .appendQueryParameter(SORTBY_PARAM, params[0])
                        .appendQueryParameter(API_PARAM, API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0) {
                    return null;
                }

                movieListRawJson = buffer.toString();

            } catch(IOException e) {
                Log.e(LOG_TAG, "Error: ", e);
                return null;
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }

                if(reader != null) {
                    try {
                        reader.close();
                    } catch(final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
        }

            ///?sort_by=popularity.desc&api_key=

            return out;
        }

        private String[] getListDataFromJson(String movieListJsonStr) {
            final String OWM_ID = "id";
            final String OWM_TITLE = "original_title";
            final String OWM_DESC = "overview";
            final String OWM_POSTER = "poster_path";
            final String OWM_VOTE = "vote_average";

        }
    }

}
