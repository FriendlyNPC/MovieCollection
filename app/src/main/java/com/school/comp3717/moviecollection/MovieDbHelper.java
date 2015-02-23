package com.school.comp3717.moviecollection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.omertron.themoviedbapi.model.MovieDb;
import com.school.comp3717.moviecollection.MovieDbContract.MovieTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If DB schema changed, must increment DB version; otherwise, DB errors
    public static final int              DATABASE_VERSION = 7;
    public static final String           DATABASE_NAME = "Movie.db";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            + MovieTable.RELEASE_DATE + TEXT_TYPE + COMMA
            + MovieTable.FILM_RATING +  TEXT_TYPE + COMMA
            + MovieTable.RUNTIME +      INT_TYPE +  COMMA
            + MovieTable.VOTE_AVERAGE + REAL_TYPE + COMMA
            + MovieTable.VOTE_COUNT +   INT_TYPE  + COMMA
            + MovieTable.TAG_LINE +     TEXT_TYPE + COMMA
            + MovieTable.SYNOPSIS +     TEXT_TYPE + COMMA
            + MovieTable.POSTER_URL +   TEXT_TYPE + COMMA
            + MovieTable.GENRE +        TEXT_TYPE + COMMA
            + MovieTable.DIRECTOR +     TEXT_TYPE + COMMA
            + MovieTable.STUDIO +       TEXT_TYPE + COMMA
            + MovieTable.POPULARITY +   REAL_TYPE + COMMA
            + MovieTable.BUDGET +       INT_TYPE  + COMMA
            + MovieTable.REVENUE +      INT_TYPE  + COMMA
            + MovieTable.MY_RATING +    REAL_TYPE + COMMA
            + MovieTable.MY_REVIEW +    TEXT_TYPE + COMMA
            + MovieTable.LAST_WATCHED + TEXT_TYPE + COMMA
            + MovieTable.WATCH_COUNT +  INT_TYPE +  COMMA
            + MovieTable.IS_LOANED +    INT_TYPE +  COMMA
            + MovieTable.DATE_ADDED +   TEXT_TYPE + COMMA
            + MovieTable.IS_COLLECTED + INT_TYPE
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

    // Adds movie to local DB
    public void addMovie(Movie movie) {
        if (!checkMovieExists(movie.getMovieId())) {
            SQLiteDatabase sq = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MovieTable.MOVIE_ID, movie.getMovieId());
            values.put(MovieTable.TITLE, movie.getTitle());
            values.put(MovieTable.RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieTable.FILM_RATING, movie.getFilmRating());
            values.put(MovieTable.RUNTIME, movie.getRuntime());
            values.put(MovieTable.VOTE_AVERAGE, movie.getVoteAverage());
            values.put(MovieTable.VOTE_COUNT, movie.getVoteCount());
            values.put(MovieTable.TAG_LINE, movie.getTagLine());
            values.put(MovieTable.SYNOPSIS, movie.getSynopsis());
            values.put(MovieTable.POSTER_URL, movie.getPosterUrl());
            values.put(MovieTable.GENRE, movie.getGenre());
            values.put(MovieTable.DIRECTOR, movie.getDirector());
            values.put(MovieTable.STUDIO, movie.getStudio());
            values.put(MovieTable.POPULARITY, movie.getPopularity());
            values.put(MovieTable.BUDGET, movie.getBudget());
            values.put(MovieTable.REVENUE, movie.getRevenue());
            values.put(MovieTable.MY_RATING, movie.getMyRating());
            values.put(MovieTable.MY_REVIEW, movie.getMyReview());
            values.put(MovieTable.LAST_WATCHED, movie.getLastWatched());
            values.put(MovieTable.WATCH_COUNT, movie.getWatchCount());
            values.put(MovieTable.IS_LOANED, movie.isLoaned());
            values.put(MovieTable.DATE_ADDED, movie.getDateAdded());
            values.put(MovieTable.IS_COLLECTED, movie.isCollected());
            sq.insert(MovieTable.TABLE_NAME, null, values);
            sq.close();
            Log.d(TAG, "Movie added to Movie table");
        } else {
            Log.d(TAG, "Movie already exists in DB");
        }
    }

    // Adds movie to collection; adds movie to Movie table if it doesn't already exist
    public void addMovieToCollection(Movie movie) {
        Date now = new Date();
        String date = DATE_FORMAT.format(now);
        addMovie(movie);
        movie.setDateAdded(date);
        movie.setCollected(1);
        SQLiteDatabase db = this.getWritableDatabase();
        String setCollectionCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.IS_COLLECTED
                                  + " = " + "1 WHERE " + MovieTable.MOVIE_ID + " = " + movie.getMovieId();
        String setDateAddedCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.DATE_ADDED
                                 + " = \"" + date + "\" WHERE " + MovieTable.MOVIE_ID + " = " + movie.getMovieId();
        db.execSQL(setCollectionCmd);
        db.execSQL(setDateAddedCmd);
        db.close();
        Log.d(TAG, "Movie added to collection");
    }

    // Removes movie from Movie table
    public void removeMovieByID(int movieId){
        SQLiteDatabase db = this.getWritableDatabase();
        String removeMovieByIDCommand = "DELETE FROM " + MovieTable.TABLE_NAME + " WHERE " +
                                        MovieTable.MOVIE_ID + " = " + movieId;
        db.execSQL(removeMovieByIDCommand);
        db.close();
        Log.d(TAG, "Movie removed from Movie table");
    }

    // Removes movie from collection but remains in Movie table (for watch count, review, rating, etc.)
    public void removeMovieFromCollection(int movieId){
        SQLiteDatabase db = this.getWritableDatabase();
        String setCollectionCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.IS_COLLECTED
                                  + " = " + "0 WHERE " + MovieTable.MOVIE_ID + " = " + movieId;
        String removeDateAddedCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.DATE_ADDED
                                    + " = NULL WHERE " + MovieTable.MOVIE_ID + " = " + movieId;
        db.execSQL(setCollectionCmd);
        db.execSQL(removeDateAddedCmd);
        db.close();
        Log.d(TAG, "Movie removed from collection");
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
                              cr.getString(cr.getColumnIndex("releaseDate")),
                              cr.getString(cr.getColumnIndex("filmRating")),
                              cr.getInt(cr.getColumnIndex("runtime")),
                              cr.getDouble(cr.getColumnIndex("voteAverage")),
                              cr.getInt(cr.getColumnIndex("voteCount")),
                              cr.getString(cr.getColumnIndex("tagLine")),
                              cr.getString(cr.getColumnIndex("synopsis")),
                              cr.getString(cr.getColumnIndex("posterUrl")),
                              cr.getString(cr.getColumnIndex("genre")),
                              cr.getString(cr.getColumnIndex("director")),
                              cr.getString(cr.getColumnIndex("studio")),
                              cr.getDouble(cr.getColumnIndex("popularity")),
                              cr.getLong(cr.getColumnIndex("budget")),
                              cr.getLong(cr.getColumnIndex("revenue")),
                              cr.getDouble(cr.getColumnIndex("myRating")),
                              cr.getString(cr.getColumnIndex("myReview")),
                              cr.getString(cr.getColumnIndex("lastWatched")),
                              cr.getInt(cr.getColumnIndex("watchCount")),
                              cr.getInt(cr.getColumnIndex("isLoaned")),
                              cr.getString(cr.getColumnIndex("dateAdded")),
                              cr.getInt(cr.getColumnIndex("isCollected")));
        }
        cr.close();
        sq.close();
        return movie;
    }

    // Checks if movie exists in local DB; includes movies not in collection
    public Boolean checkMovieExists(int movieId) {
        Cursor cr;
        SQLiteDatabase sq = this.getReadableDatabase();
        cr = sq.rawQuery("SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE " +
                         MovieTable.MOVIE_ID + " = " + movieId, null);
        return (cr.getCount() > 0);
    }

    // Gets movies in collection that exist in MovieDb list provided
    public List<Integer> getMovieIDsExist(List<MovieDb> toSearch){

        Cursor cr;
        List<Integer> found = new ArrayList<>();
        SQLiteDatabase sq = this.getReadableDatabase();

        if (toSearch.isEmpty()){
            return found;
        }

        String list = "(";

        for(int i = 0; i < toSearch.size()-1; i++ ){
           list += toSearch.get(i).getId() + ", ";
        }
        list += toSearch.get(toSearch.size() - 1).getId() + ")";

        cr = sq.rawQuery("SELECT " + MovieTable.MOVIE_ID + " FROM " + MovieTable.TABLE_NAME
                         + " WHERE " + MovieTable.MOVIE_ID + " IN " + list + " AND "
                         + MovieTable.IS_COLLECTED + " = 1", null);

        if (cr.moveToFirst()) {
            while (!cr.isAfterLast()) {
                found.add(new Integer(cr.getInt(cr.getColumnIndex("movieId"))));
                cr.moveToNext();
            }
        }
        return found;
    }

    // Gets a movie from collection using online DB ID; stores in Movie object
    public List<Movie> searchMovie(String query) {
        Cursor cr;
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase sq = this.getReadableDatabase();
        cr = sq.rawQuery("SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE LOWER(" +
                         MovieTable.TITLE + ") LIKE '%" + query.trim().toLowerCase() + "%' AND "
                         + MovieTable.IS_COLLECTED + " = 1 ORDER BY " + MovieTable.POPULARITY
                         + " DESC", null);
        if (cr.moveToFirst()) {
            while (!cr.isAfterLast()) {
                movies.add(
                        new Movie(cr.getInt(cr.getColumnIndex("movieId")),
                                cr.getString(cr.getColumnIndex("title")),
                                cr.getString(cr.getColumnIndex("releaseDate")),
                                cr.getString(cr.getColumnIndex("filmRating")),
                                cr.getInt(cr.getColumnIndex("runtime")),
                                cr.getDouble(cr.getColumnIndex("voteAverage")),
                                cr.getInt(cr.getColumnIndex("voteCount")),
                                cr.getString(cr.getColumnIndex("tagLine")),
                                cr.getString(cr.getColumnIndex("synopsis")),
                                cr.getString(cr.getColumnIndex("posterUrl")),
                                cr.getString(cr.getColumnIndex("genre")),
                                cr.getString(cr.getColumnIndex("director")),
                                cr.getString(cr.getColumnIndex("studio")),
                                cr.getDouble(cr.getColumnIndex("popularity")),
                                cr.getLong(cr.getColumnIndex("budget")),
                                cr.getLong(cr.getColumnIndex("revenue")),
                                cr.getDouble(cr.getColumnIndex("myRating")),
                                cr.getString(cr.getColumnIndex("myReview")),
                                cr.getString(cr.getColumnIndex("lastWatched")),
                                cr.getInt(cr.getColumnIndex("watchCount")),
                                cr.getInt(cr.getColumnIndex("isLoaned")),
                                cr.getString(cr.getColumnIndex("dateAdded")),
                                cr.getInt(cr.getColumnIndex("isCollected")))
                );
                cr.moveToNext();
            }
        }
        cr.close();
        sq.close();
        return movies;
    }

    // Gets entire movie collection in local DB; stores in Movie ArrayList
    public List<Movie> getMovieCollection() {
        Cursor cr;
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase sq = this.getReadableDatabase();
        cr = sq.rawQuery("SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE "
                         + MovieTable.IS_COLLECTED + " = 1", null);
        if (cr.moveToFirst()) {
            while (!cr.isAfterLast()) {
                movies.add(
                    new Movie(cr.getInt(cr.getColumnIndex("movieId")),
                              cr.getString(cr.getColumnIndex("title")),
                              cr.getString(cr.getColumnIndex("releaseDate")),
                              cr.getString(cr.getColumnIndex("filmRating")),
                              cr.getInt(cr.getColumnIndex("runtime")),
                              cr.getDouble(cr.getColumnIndex("voteAverage")),
                              cr.getInt(cr.getColumnIndex("voteCount")),
                              cr.getString(cr.getColumnIndex("tagLine")),
                              cr.getString(cr.getColumnIndex("synopsis")),
                              cr.getString(cr.getColumnIndex("posterUrl")),
                              cr.getString(cr.getColumnIndex("genre")),
                              cr.getString(cr.getColumnIndex("director")),
                              cr.getString(cr.getColumnIndex("studio")),
                              cr.getDouble(cr.getColumnIndex("popularity")),
                              cr.getLong(cr.getColumnIndex("budget")),
                              cr.getLong(cr.getColumnIndex("revenue")),
                              cr.getDouble(cr.getColumnIndex("myRating")),
                              cr.getString(cr.getColumnIndex("myReview")),
                              cr.getString(cr.getColumnIndex("lastWatched")),
                              cr.getInt(cr.getColumnIndex("watchCount")),
                              cr.getInt(cr.getColumnIndex("isLoaned")),
                              cr.getString(cr.getColumnIndex("dateAdded")),
                              cr.getInt(cr.getColumnIndex("isCollected")))
                );
                cr.moveToNext();
            }
        }
        cr.close();
        sq.close();
        return movies;
    }

    public void updateWatchCount(Movie movie) {
        Date now = new Date();
        String date = DATE_FORMAT.format(now);
        movie.setWatchCount(movie.getWatchCount() + 1);
        movie.setLastWatched(date);
        SQLiteDatabase sq = this.getWritableDatabase();
        String lastWatchedCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.LAST_WATCHED
                                + " = \"" + date + "\" WHERE " + MovieTable.MOVIE_ID + " = "
                                + movie.getMovieId();
        String watchCountCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.WATCH_COUNT
                               + " = " + movie.getWatchCount() + " WHERE " + MovieTable.MOVIE_ID
                               + " = " + movie.getMovieId();
        sq.execSQL(lastWatchedCmd);
        sq.execSQL(watchCountCmd);
        sq.close();
        Log.d(TAG, "Movie's lastWatched and watchCount updated");
    }

    public void updateMyRating(Movie movie, float rating) {
        SQLiteDatabase sq = this.getWritableDatabase();
        movie.setMyRating(rating);
        String myRatingCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.MY_RATING
                             + " = " + movie.getMyRating() + " WHERE " + MovieTable.MOVIE_ID + " = "
                             + movie.getMovieId();
        sq.execSQL(myRatingCmd);
        sq.close();
        Log.d(TAG, "Movie's myRating updated");
    }

    public void updateMyReview(Movie movie, String review) {
        SQLiteDatabase sq = this.getWritableDatabase();
        movie.setMyReview(review);
        String myReviewCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.MY_REVIEW
                             + " = \"" + movie.getMyReview() + "\" WHERE " + MovieTable.MOVIE_ID
                             + " = " + movie.getMovieId();
        sq.execSQL(myReviewCmd);
        sq.close();
        Log.d(TAG, "Movie's myReview updated");
    }
}
