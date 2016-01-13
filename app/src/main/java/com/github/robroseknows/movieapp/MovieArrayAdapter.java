package com.github.robroseknows.movieapp;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by Robert on 11/8/2015.
 */
public class MovieArrayAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<MovieObject> movies;
    private int layoutResource;

    private static final POSTER_PATH = "image.tmdb.org/t/p/w185";
    private static final LOG_TAG = "MovieArrayAdapter";

    public MovieArrayAdapter(Context context, int resource, Object[] objects) {
        this.context = context;
        this.movies = objects;
        this.layoutResource = resource;

        super(context, resource, objects);
    }

    public int getCount() {
        movies.size();
    }

    public Object getItem(int position) {
        return movies.get(position);
    }

    public int getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayouyt itemLayout;
        MovieObject movie = getItem(position);
        View row = null;

        if(convertView == null) {
            itemLayout = new LinearLayout(getContext());

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            row = layoutInflater.inflate(layoutResource, itemLayout, true);
        } else {
            row = convertView;
        }

        if(artist != null) {
            ((TextView) row.findViewById(R.id.movie_title)).setText(movie.movieTitle);

        }

    }
}
