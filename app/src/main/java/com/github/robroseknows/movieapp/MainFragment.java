package com.github.robroseknows.movieapp;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainFragment extends Fragment {

    private final String API_KEY = "hunter2";  // TODO: Remove before commit!!


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
        private double moviePopularity = 38.598673;
        private double movieVoteAvg = 7.7;
        private String moviePosterPath = "/AjbENYG3b8lhYSkdrWwlhVLRPKR.jpg";
    }

    public class FetchMovieListTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            if(params.length == 0) {
                return null;
            }

            String[] out = new String[5];

            try {
                final String CALL_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORTBY_PARAM = "sort_by";
                final String API_PARAM = "api_key";


                Uri builtUri = Uri.parse(CALL_BASE_URL).buildUpon()
                        .appendQueryParameter()
                )
            }

            ///?sort_by=popularity.desc&api_key=

            return out;
        }
    }

}
