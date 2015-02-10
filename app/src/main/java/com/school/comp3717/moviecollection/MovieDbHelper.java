package com.school.comp3717.moviecollection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.school.comp3717.moviecollection.MovieDbContract.MovieTable;

import java.util.ArrayList;
import java.util.List;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If DB schema changed, must increment DB version; otherwise, DB errors
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movie.db";

    private static final String TAG =          "MovieDbHelper";
    private static final String INT_TYPE =     " INTEGER";
    private static final String TEXT_TYPE =    " TEXT";
    private static final String REAL_TYPE =    " REAL";
    private static final String PRIM_KEY =     " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String COMMA =        ", ";
    private static final String CREATE_QUERY = "CREATE TABLE "
            + MovieTable.TABLE_NAME + "("
            + MovieTable._ID +          PRIM_KEY +  COMMA
            + MovieTable.MOVIE_ID +     INT_TYPE +  COMMA
            + MovieTable.TITLE +        TEXT_TYPE + COMMA
            + MovieTable.YEAR +         INT_TYPE +  COMMA
            + MovieTable.MPAA_RATING +  TEXT_TYPE + COMMA
            + MovieTable.RUNTIME +      INT_TYPE +  COMMA
            + MovieTable.CRITIC_SCORE + REAL_TYPE + COMMA
            + MovieTable.USER_SCORE +   INT_TYPE +  COMMA
            + MovieTable.SYNOPSIS +     TEXT_TYPE + COMMA
            + MovieTable.POSTER_URL +   TEXT_TYPE + COMMA
            + MovieTable.GENRE +        TEXT_TYPE + COMMA
            + MovieTable.DIRECTOR +     TEXT_TYPE + COMMA
            + MovieTable.STUDIO +       TEXT_TYPE + COMMA
            + MovieTable.LAST_WATCHED + TEXT_TYPE + COMMA
            + MovieTable.WATCH_COUNT +  INT_TYPE +  COMMA
            + MovieTable.IS_LOANED +    INT_TYPE
            + ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieTable.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Movie database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
        Log.d(TAG, "Movie table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    /* Used for testing purposes; resets database without requiring the
     * DB version to be incremented
     */
    public void cleanDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // Add movie to local DB
    public void addMovie(Movie movie) {
        SQLiteDatabase sq = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieTable.MOVIE_ID, movie.getMovieId());
        values.put(MovieTable.TITLE, movie.getTitle());
        values.put(MovieTable.YEAR, movie.getYear());
        values.put(MovieTable.MPAA_RATING, movie.getMpaaRating());
        values.put(MovieTable.RUNTIME, movie.getRuntime());
        values.put(MovieTable.CRITIC_SCORE, movie.getCriticScore());
        values.put(MovieTable.USER_SCORE, movie.getUserScore());
        values.put(MovieTable.SYNOPSIS, movie.getSynopsis());
        values.put(MovieTable.POSTER_URL, movie.getPosterUrl());
        values.put(MovieTable.GENRE, movie.getGenre());
        values.put(MovieTable.DIRECTOR, movie.getDirector());
        values.put(MovieTable.STUDIO, movie.getStudio());
        values.put(MovieTable.LAST_WATCHED, movie.getLastWatched());
        values.put(MovieTable.WATCH_COUNT, movie.getWatchCount());
        values.put(MovieTable.IS_LOANED, movie.isLoaned());
        sq.insert(MovieTable.TABLE_NAME, null, values);
        Log.d(TAG, "One row inserted");
    }

    // Gets a movie from local DB using online DB ID; stores in Movie object
    public Movie getMovieById(int movieId) {
        Cursor cr;
        Movie movie = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        cr = sq.rawQuery("SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE " +
                         MovieTable.MOVIE_ID + " = " + movieId, null);
        if (cr.moveToFirst()) {
            movie = new Movie(cr.getInt(cr.getColumnIndex("movieId")),
                              cr.getString(cr.getColumnIndex("title")),
                              cr.getInt(cr.getColumnIndex("year")),
                              cr.getString(cr.getColumnIndex("mpaaRating")),
                              cr.getInt(cr.getColumnIndex("runtime")),
                              cr.getDouble(cr.getColumnIndex("criticScore")),
                              cr.getDouble(cr.getColumnIndex("userScore")),
                              cr.getString(cr.getColumnIndex("synopsis")),
                              cr.getString(cr.getColumnIndex("posterUrl")),
                              cr.getString(cr.getColumnIndex("genre")),
                              cr.getString(cr.getColumnIndex("director")),
                              cr.getString(cr.getColumnIndex("studio")),
                              cr.getString(cr.getColumnIndex("lastWatched")),
                              cr.getInt(cr.getColumnIndex("watchCount")),
                              cr.getInt(cr.getColumnIndex("isLoaned")));
        }
        return movie;
    }

    // Gets all movies from local DB; stores in Movie ArrayList
    public List<Movie> getAllMovies() {
        Cursor cr;
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase sq = this.getReadableDatabase();
        cr = sq.rawQuery("SELECT * FROM " + MovieTable.TABLE_NAME, null);
        if (cr.moveToFirst()) {
            while (!cr.isAfterLast()) {
                movies.add(
                    new Movie(cr.getInt(cr.getColumnIndex("movieId")),
                              cr.getString(cr.getColumnIndex("title")),
                              cr.getInt(cr.getColumnIndex("year")),
                              cr.getString(cr.getColumnIndex("mpaaRating")),
                              cr.getInt(cr.getColumnIndex("runtime")),
                              cr.getDouble(cr.getColumnIndex("criticScore")),
                              cr.getDouble(cr.getColumnIndex("userScore")),
                              cr.getString(cr.getColumnIndex("synopsis")),
                              cr.getString(cr.getColumnIndex("posterUrl")),
                              cr.getString(cr.getColumnIndex("genre")),
                              cr.getString(cr.getColumnIndex("director")),
                              cr.getString(cr.getColumnIndex("studio")),
                              cr.getString(cr.getColumnIndex("lastWatched")),
                              cr.getInt(cr.getColumnIndex("watchCount")),
                              cr.getInt(cr.getColumnIndex("isLoaned")))
                );
                cr.moveToNext();
            }
        }
        return movies;
    }
}
