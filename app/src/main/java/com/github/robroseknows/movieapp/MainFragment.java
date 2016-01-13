package com.github.robroseknows.movieapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.Manifest.permission;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.jar.Manifest;


public class MainFragment extends Fragment {

    private final String API_KEY = SecretKeys.MOVIEDB_KEY;
    private final String LOG_TAG = "MainFragment";
    private final int PERMISSION_REQUEST_INTERNET = 9;
    private MovieArrayAdapter adapter;
    private ArrayList<MovieObject> movies;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        // RIP simple calling.
        if(ContextCompat.checkSelfPermission(getActivity(), permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission.INTERNET)) {

                // Show them a dialog with the rationale.
                AlertDialog.Builder rationaleAlert = new AlertDialog.Builder(getActivity());
                rationaleAlert.setMessage(R.string.internet_permission_rationale);
                rationaleAlert.setTitle(R.string.app_name);
                rationaleAlert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                rationaleAlert.setCancelable(true);
                rationaleAlert.create().show();

                // Now request permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission.INTERNET}, PERMISSION_REQUEST_INTERNET);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission.INTERNET}, PERMISSION_REQUEST_INTERNET);
            }
        } else {
            updateMovies();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_INTERNET: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateMovies();
                    Toast.makeText(getActivity(), R.string.permission_thanks, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.sad_face, Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }

    private void updateMovies() {
        FetchMovieListTask fetchMovies = new FetchMovieListTask();
        fetchMovies.execute("popularity");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movies = new ArrayList<MovieObject>();
        adapter = new MovieArrayAdapter(getActivity(), R.layout.movie_poster_item, movies);

        GridView movieGridView = (GridView) rootView.findViewById(R.id.movieGridView);
        movieGridView.setAdapter(adapter);

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
                //Log.v(LOG_TAG, currentJsonObject.getString(MDB_POSTER));
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
