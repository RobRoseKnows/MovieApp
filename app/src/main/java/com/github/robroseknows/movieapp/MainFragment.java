package com.github.robroseknows.movieapp;

import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainFragment extends Fragment {

    private final String API_KEY = SecretKeys.MOVIEDB_KEY;
    private final String LOG_TAG = "MainFragment";
    private MovieArrayAdapter adapter;
    private ArrayList<MovieObject> movies;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new MovieArrayAdapter(MainFragment.this, R.layout.movie_poster_item, movies);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);



        return rootView;
    }

    public class FetchMovieListTask extends AsyncTask<String, Void, MovieObject[]> {

        @Override
        protected MovieObject[] doInBackground(String... params) {
            if(params.length == 0) {
                return null;
            }

            MovieObject[] out;

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
                out = getListDataFromJson(movieListRawJson);

            } catch(IOException e) {
                Log.e(LOG_TAG, "IO Error: ", e);
                return null;
            } catch(JSONException e) {
                Log.e(LOG_TAG, "JSON Error: ", e);
                out = null;
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

        private MovieObject[] getListDataFromJson(String movieListJsonStr) throws JSONException {
            final String MDB_RESULTS = "results";
            final String MDB_ID = "id";
            final String MDB_TITLE = "original_title";
            final String MDB_DESC = "overview";
            final String MDB_POSTER = "poster_path";
            final String MDB_VOTE = "vote_average";

            if(movieListJsonStr == null)
                return null;

            JSONObject movieJson = new JSONObject(movieListJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);
            MovieObject[] resultObjects = new MovieObject[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject currentJsonObject = (JSONObject) movieArray.get(i);
                MovieObject newMovieObject = new MovieObject(
                        currentJsonObject.getInt(MDB_ID),
                        currentJsonObject.getString(MDB_TITLE),
                        currentJsonObject.getString(MDB_DESC),
                        currentJsonObject.getString(MDB_POSTER),
                        currentJsonObject.getDouble(MDB_VOTE));
                resultObjects[i] = newMovieObject;
            }

            return resultObjects;
        }

        @Override
        protected void onPostExecute(MovieObject[] result) {
            super.onPostExecute(result);
            if(result != null) {
                movies.clear();
                adapter.clear();
                for(MovieObject movie : result) {
                    movies.add(movie);
                    adapter.add(movie);
                }
            }
        }
    }

}
