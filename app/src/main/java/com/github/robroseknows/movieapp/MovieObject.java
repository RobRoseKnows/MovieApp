package com.github.robroseknows.movieapp;

/**
 * Created by Robert on 11/8/2015.
 */
public class MovieObject {
    private int movieId = 286217;
    private String movieTitle = "The Martian";
    private String movieDescription = "";
    private String moviePosterPath = "/AjbENYG3b8lhYSkdrWwlhVLRPKR.jpg";
    private double movieVoteAvg = 7.7;

    public MovieObject(int id, String title, String desc, String poster, double avg) {
        movieId = id;
        movieTitle = title;
        movieDescription = desc;
        moviePosterPath = poster;
        movieVoteAvg = avg;
    }

    // Get methods
    public int      getMovieId()            { return movieId; }
    public String   getMovieTitle()         { return movieTitle; }
    public String   getMovieDescription()   { return movieDescription; }
    public String   getMoviePosterPath()    { return moviePosterPath; }
    public double   getMovieVoteAvg()       { return movieVoteAvg; }
}