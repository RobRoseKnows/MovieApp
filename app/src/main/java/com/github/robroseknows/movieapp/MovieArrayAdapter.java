package com.github.robroseknows.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Robert on 11/8/2015.
 */
public class MovieArrayAdapter extends ArrayAdapter {
    private Context context;
    private List<MovieObject> movies;
    private int layoutResource;

    private static final String POSTER_PATH = "http://image.tmdb.org/t/p/w185/";
    private static final String LOG_TAG = "MovieArrayAdapter";

    public MovieArrayAdapter(Context context, int resource, List<MovieObject> objects) {
        super(context, resource, objects);

        this.context = context;
        if(objects != null)
            this.movies = objects;
        else
            this.movies = new ArrayList<>();
        this.layoutResource = resource;

    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public MovieObject getItem(int position) {
        return movies.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemLayout;
        MovieObject movie = getItem(position);
        View row = null;

        if(convertView == null) {
            itemLayout = new LinearLayout(getContext());

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            row = layoutInflater.inflate(layoutResource, itemLayout, true);
        } else {
            row = convertView;
        }

        if(movie != null) {

            TextView titleTextView = (TextView) row.findViewById(R.id.movie_title);
            ImageView posterImageView = (ImageView) row.findViewById(R.id.movie_poster);

            (titleTextView).setText(movie.getMovieTitle());
            // Need to substring because of the \ that appears before the path for some reason in the JSON.
            Picasso.with(context).load(POSTER_PATH + movie.getMoviePosterPath().substring(1)).into(posterImageView);
        }

        return row;
    }
}
