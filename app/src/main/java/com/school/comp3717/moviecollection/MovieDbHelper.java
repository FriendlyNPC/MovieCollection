package com.school.comp3717.moviecollection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseIntArray;

import com.omertron.themoviedbapi.model.MovieDb;
import com.school.comp3717.moviecollection.MovieDbContract.MovieTable;
import com.school.comp3717.moviecollection.collection.MovieFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If Db schema changed, must increment Db version; otherwise, Db errors
    private static final int              DATABASE_VERSION = 7;
    private static final String           DATABASE_NAME    = "Movie.db";
    private static final SimpleDateFormat DATE_FORMAT      = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String           TAG              = "MovieDbHelper";
    public  static final String           NO_PREF          = "No Preference";
    private static final String           INT_TYPE         = " INTEGER";
    private static final String           TEXT_TYPE        = " TEXT";
    private static final String           REAL_TYPE        = " REAL";
    private static final String           PRIM_KEY         = " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String           COMMA            = ", ";
    private static final String           CREATE_QUERY     = "CREATE TABLE "
            + MovieTable.TABLE_NAME   + "("
            + MovieTable._ID          + PRIM_KEY  + COMMA
            + MovieTable.MOVIE_ID     + INT_TYPE  + COMMA
            + MovieTable.TITLE        + TEXT_TYPE + COMMA
            + MovieTable.RELEASE_DATE + TEXT_TYPE + COMMA
            + MovieTable.FILM_RATING  + TEXT_TYPE + COMMA
            + MovieTable.RUNTIME      + INT_TYPE  + COMMA
            + MovieTable.VOTE_AVERAGE + REAL_TYPE + COMMA
            + MovieTable.VOTE_COUNT   + INT_TYPE  + COMMA
            + MovieTable.TAG_LINE     + TEXT_TYPE + COMMA
            + MovieTable.SYNOPSIS     + TEXT_TYPE + COMMA
            + MovieTable.POSTER_URL   + TEXT_TYPE + COMMA
            + MovieTable.GENRE        + TEXT_TYPE + COMMA
            + MovieTable.DIRECTOR     + TEXT_TYPE + COMMA
            + MovieTable.STUDIO       + TEXT_TYPE + COMMA
            + MovieTable.POPULARITY   + REAL_TYPE + COMMA
            + MovieTable.BUDGET       + INT_TYPE  + COMMA
            + MovieTable.REVENUE      + INT_TYPE  + COMMA
            + MovieTable.MY_RATING    + REAL_TYPE + COMMA
            + MovieTable.MY_REVIEW    + TEXT_TYPE + COMMA
            + MovieTable.LAST_WATCHED + TEXT_TYPE + COMMA
            + MovieTable.WATCH_COUNT  + INT_TYPE  + COMMA
            + MovieTable.IS_LOANED    + INT_TYPE  + COMMA
            + MovieTable.DATE_ADDED   + TEXT_TYPE + COMMA
            + MovieTable.IS_COLLECTED + INT_TYPE  + ");";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieTable.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Log.d(TAG, "Movie database created");
    }

    @Override
    public void onCreate(SQLiteDatabase sq) {
        sq.execSQL(CREATE_QUERY);
        Log.d(TAG, "Movie table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sq, int oldVersion, int newVersion) {
        sq.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sq);
    }

    /*
     * Used for testing purposes; resets database without requiring the
     * Db version to be incremented
     */
    public void cleanDatabase() {
        SQLiteDatabase sq = this.getWritableDatabase();
        try {
            sq.execSQL(SQL_DELETE_ENTRIES);
            onCreate(sq);
        } catch (SQLiteException e) {
            Log.e(TAG, "cleanDatabase() error", e);
        } finally {
            if (sq != null) { sq.close(); }
        }
    }

    // Adds movie to local Db
    public void addMovie(Movie movie) {
        if (!checkMovieExists(movie.getMovieId())) {
            SQLiteDatabase sq     = this.getWritableDatabase();
            ContentValues  values = new ContentValues();

            values.put(MovieTable.MOVIE_ID,     movie.getMovieId());
            values.put(MovieTable.TITLE,        movie.getTitle());
            values.put(MovieTable.RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieTable.FILM_RATING,  movie.getFilmRating());
            values.put(MovieTable.RUNTIME,      movie.getRuntime());
            values.put(MovieTable.VOTE_AVERAGE, movie.getVoteAverage());
            values.put(MovieTable.VOTE_COUNT,   movie.getVoteCount());
            values.put(MovieTable.TAG_LINE,     movie.getTagLine());
            values.put(MovieTable.SYNOPSIS,     movie.getSynopsis());
            values.put(MovieTable.POSTER_URL,   movie.getPosterUrl());
            values.put(MovieTable.GENRE,        movie.getGenre());
            values.put(MovieTable.DIRECTOR,     movie.getDirector());
            values.put(MovieTable.STUDIO,       movie.getStudio());
            values.put(MovieTable.POPULARITY,   movie.getPopularity());
            values.put(MovieTable.BUDGET,       movie.getBudget());
            values.put(MovieTable.REVENUE,      movie.getRevenue());
            values.put(MovieTable.MY_RATING,    movie.getMyRating());
            values.put(MovieTable.MY_REVIEW,    movie.getMyReview());
            values.put(MovieTable.LAST_WATCHED, movie.getLastWatched());
            values.put(MovieTable.WATCH_COUNT,  movie.getWatchCount());
            values.put(MovieTable.IS_LOANED,    movie.isLoaned());
            values.put(MovieTable.DATE_ADDED,   movie.getDateAdded());
            values.put(MovieTable.IS_COLLECTED, movie.isCollected());

            try {
                sq.insert(MovieTable.TABLE_NAME, null, values);
            } catch (SQLiteException e) {
                Log.e(TAG, "addMovie() error", e);
            } finally {
                if (sq != null) { sq.close(); }
            }

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

        String setCollectionCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.IS_COLLECTED
                                  + " = " + "1 WHERE " + MovieTable.MOVIE_ID + " = " + movie.getMovieId();
        String setDateAddedCmd  = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.DATE_ADDED
                                  + " = \"" + date + "\" WHERE " + MovieTable.MOVIE_ID + " = " + movie.getMovieId();
        updateDb(setCollectionCmd);
        updateDb(setDateAddedCmd);

        Log.d(TAG, "Movie added to collection");
    }

    // Removes movie from Movie table
    public void removeMovieByID(int movieId) {
        String removeMovieByIDCommand = "DELETE FROM " + MovieTable.TABLE_NAME + " WHERE " +
                                        MovieTable.MOVIE_ID + " = " + movieId;
        updateDb(removeMovieByIDCommand);

        Log.d(TAG, "Movie removed from Movie table");
    }

    // Removes movie from collection but remains in Movie table (for watch count, review, rating, etc.)
    public void removeMovieFromCollection(int movieId) {
        String setCollectionCmd   = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.IS_COLLECTED
                                    + " = " + "0 WHERE " + MovieTable.MOVIE_ID + " = " + movieId;
        String removeDateAddedCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.DATE_ADDED
                                    + " = NULL WHERE " + MovieTable.MOVIE_ID + " = " + movieId;
        updateDb(setCollectionCmd);
        updateDb(removeDateAddedCmd);

        Log.d(TAG, "Movie removed from collection");
    }

    // Gets a movie from local Db using online Db ID; stores in Movie object
    public Movie getMovieById(int movieId) {
        String query = "SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE " +
                       MovieTable.MOVIE_ID + " = " + movieId;
        ArrayList<Movie> movies = movieListQuery(query);

        if (movies.isEmpty()) {
            return null;
        } else {
            return movies.get(0);
        }
    }

    // Checks if movie exists in local Db; includes movies not in collection
    public Boolean checkMovieExists(int movieId) {
        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        int count = 0;

        try {
            cr = sq.rawQuery("SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE " +
                             MovieTable.MOVIE_ID + " = " + movieId, null);
            count = cr.getCount();
        } catch (SQLiteException e) {
            Log.e(TAG, "checkMovieExists() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return (count > 0);
    }

    // Gets movies in collection that exist in MovieDb list provided
    public List<Integer> getMovieIDsExist(List<MovieDb> toSearch) {
        Cursor cr = null;
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

        try {
            cr = sq.rawQuery("SELECT " + MovieTable.MOVIE_ID + " FROM " + MovieTable.TABLE_NAME
                    + " WHERE " + MovieTable.MOVIE_ID + " IN " + list + " AND "
                    + MovieTable.IS_COLLECTED + " = 1", null);

            if (cr.moveToFirst()) {
                while (!cr.isAfterLast()) {
                    found.add(new Integer(cr.getInt(cr.getColumnIndex("movieId"))));
                    cr.moveToNext();
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieIDsExist() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return found;
    }

    // Gets a movie from collection using online Db ID; stores in Movie object
    public ArrayList<Movie> searchMovie(String title) {
        String query = "SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE LOWER(" +
                       MovieTable.TITLE + ") LIKE '%" + title.trim().toLowerCase() + "%' AND "
                       + MovieTable.IS_COLLECTED + " = 1 ORDER BY " + MovieTable.POPULARITY + " DESC";

        return movieListQuery(query);
    }

    // Gets the entire collection, sorted by title (default collection filter)
    public List<Movie> getMovieCollectionSorted() {
        String query = "SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE "
                       + MovieTable.IS_COLLECTED + " = 1 ORDER BY " + MovieTable.TITLE + " ASC";

        return movieListQuery(query);
    }

    // Update watch count of movie
    public void updateWatchCount(Movie movie) {
        Date now = new Date();
        String date = DATE_FORMAT.format(now);
        movie.setWatchCount(movie.getWatchCount() + 1);
        movie.setLastWatched(date);

        String lastWatchedCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.LAST_WATCHED
                                + " = \"" + date + "\" WHERE " + MovieTable.MOVIE_ID + " = "
                                + movie.getMovieId();
        String watchCountCmd  = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.WATCH_COUNT
                                + " = " + movie.getWatchCount() + " WHERE " + MovieTable.MOVIE_ID
                                + " = " + movie.getMovieId();

        updateDb(lastWatchedCmd);
        updateDb(watchCountCmd);

        Log.d(TAG, "Movie's lastWatched and watchCount updated");
    }

    // Update myRating of movie
    public void updateMyRating(Movie movie, float rating) {
        movie.setMyRating(rating);
        String myRatingCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.MY_RATING
                             + " = " + movie.getMyRating() + " WHERE " + MovieTable.MOVIE_ID + " = "
                             + movie.getMovieId();
        updateDb(myRatingCmd);

        Log.d(TAG, "Movie's myRating updated");
    }

    // Update myReview of movie
    public void updateMyReview(Movie movie, String review) {
        movie.setMyReview(review);
        String myReviewCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.MY_REVIEW
                             + " = \"" + movie.getMyReview() + "\" WHERE " + MovieTable.MOVIE_ID
                             + " = " + movie.getMovieId();
        updateDb(myReviewCmd);

        Log.d(TAG, "Movie's myReview updated");
    }

    // Gets choices for spinner dropdown menu
    public List<String> getAllChoices(String column) {
        List<String> choices = new ArrayList<>();
        Set<String> tempSet = new TreeSet<>();

        // Select all query
        String selectQuery = "SELECT DISTINCT " + column + " FROM " + MovieTable.TABLE_NAME;
        SQLiteDatabase sq = this.getReadableDatabase();
        Cursor cr = null;

        try {
            cr = sq.rawQuery(selectQuery, null);

            // Loop through all rows, parse them, and add to set (remove duplicates)
            if (cr.moveToFirst()) {
                while (!cr.isAfterLast()) {
                    String temp = cr.getString(0);
                    StringTokenizer st = new StringTokenizer(temp, "\t");
                    while (st.hasMoreTokens())
                        tempSet.add(st.nextToken());
                    cr.moveToNext();
                }
            }

            choices.add(NO_PREF);
            for (String item : tempSet) {
                choices.add(item);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getAllChoices() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return choices;
    }

    // Gets random picks from movie collection in local Db using filters provided
    public ArrayList<Movie> getRandomPicks(String genre,
                                           String filmRating,
                                           int runtime,
                                           boolean isUnwatched,
                                           String minReleaseDate,
                                           String maxReleaseDate) {
        String query;
        String genreQuery = "";

        if (!genre.equals(NO_PREF)) {
            genreQuery = MovieTable.GENRE + " LIKE \"%" + genre + "%\" AND ";
        }

        String ratingQuery = MovieTable.FILM_RATING + " IN (";

        switch (filmRating) {
            case "No preference":
                break;
            case "N-17":
                ratingQuery += "\"NC-17\", ";
            case "R":
                ratingQuery += "\"R\", ";
            case "PG-13":
                ratingQuery += "\"PG-13\", ";
            case "PG":
                ratingQuery += "\"PG\", ";
            case "G":
                ratingQuery += "\"G\") AND ";
                break;
            default:
                break;
        }

        if (ratingQuery.equals(MovieTable.FILM_RATING + " IN (")) {
            ratingQuery = "";
        }

        query = "SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE "
                + genreQuery + ratingQuery + MovieTable.RUNTIME + " <= " + runtime + " AND "
                + MovieTable.RELEASE_DATE + " BETWEEN \"" + minReleaseDate + "\" AND \""
                + maxReleaseDate + "\" AND " + MovieTable.IS_COLLECTED + " = 1";
        if (isUnwatched) {
            query += " AND " + MovieTable.WATCH_COUNT + " = 0";
        }

        return movieListQuery(query);
    }

    //get the number of movies in the users collection
    public String getMovieCollectionCount(){
        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        String strMovies = "";
        try {
            cr = sq.rawQuery("SELECT COUNT( * ) FROM "
                    + MovieTable.TABLE_NAME + " WHERE " + MovieTable.IS_COLLECTED + " = 1", null);
            if (cr.moveToFirst()) {
                if (!cr.isAfterLast()) {
                    strMovies = cr.getString(0);
                }else{
                    strMovies = "0";
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieCollectionCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return strMovies;
    }

    //get the number of movies the user has watched
    public String getMovieWatchCount(){
        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        String strWatchCount = "";
        try {
            cr = sq.rawQuery("SELECT SUM( " + MovieTable.WATCH_COUNT +" ) FROM "
                    + MovieTable.TABLE_NAME, null);
            if (cr.moveToFirst()) {
                if (!cr.isAfterLast()) {
                    strWatchCount = cr.getString(0);
                }else{
                    strWatchCount = "0";
                }
            }else{
                strWatchCount = "0";
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieWatchCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return strWatchCount;
    }

    //Get the total number of minutes worth of movies the user has watched based on movie
    //watch count and movie runtime
    public String getNumberOfMinutesWatched(){
        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();

        Long mintuesWatched = 0L;
        try {
            cr = sq.rawQuery("SELECT " + MovieTable.WATCH_COUNT + ", "+ MovieTable.RUNTIME + " FROM "
                    + MovieTable.TABLE_NAME + " WHERE " + MovieTable.WATCH_COUNT + " > 0", null);
            if (cr.moveToFirst()) {
                while (!cr.isAfterLast()) {
                    mintuesWatched += (cr.getLong(0) * cr.getLong(1));
                    cr.moveToNext();
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieCollectionCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return mintuesWatched.toString();
    };

    //Get the number of movies that the user has rated
    public String getMoviesRatedCount(){
        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        String strMovies = "";
        try {
            cr = sq.rawQuery("SELECT COUNT( * ) FROM "
                    + MovieTable.TABLE_NAME + " WHERE " + MovieTable.MY_RATING + " > 0", null);
            if (cr.moveToFirst()) {
                if (!cr.isAfterLast()) {
                    strMovies = cr.getString(0);
                }else{
                    strMovies = "0";
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieCollectionCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return strMovies;
    }

    //Get the number of reviews the user has made. (excludes null or empty reviews)
    public String getReviewsCount(){
        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        String strReviews = "";
        try {
            cr = sq.rawQuery("SELECT COUNT( * ) FROM "
                    + MovieTable.TABLE_NAME + " WHERE " + MovieTable.MY_REVIEW + " <> '' ", null);
            if (cr.moveToFirst()) {
                if (!cr.isAfterLast()) {
                    strReviews = cr.getString(0);
                }else{
                    strReviews = "0";
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieCollectionCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }
        return strReviews;
    }


    public ArrayList<?>[] getMovieCollectionGenreCount(){
        ArrayList[] genreStats = new ArrayList[2];
        genreStats[0] = new ArrayList<String>();
        genreStats[1] = new ArrayList<Long>();

        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        Log.i("Movie Metric" , "Collection Genre Count" );
        try {
            cr = sq.rawQuery("SELECT "+ MovieTable.GENRE + ", COUNT( " + MovieTable.GENRE +" ) FROM "
                    + MovieTable.TABLE_NAME + " WHERE " +  MovieTable.IS_COLLECTED + " = 1 "
                    + "GROUP BY " + MovieTable.GENRE , null);
            if (cr.moveToFirst()) {
                while (!cr.isAfterLast()) {
                    Log.i("Movie Metric", cr.getString(0) + " : " + cr.getLong(1));
                    String[] genreList = cr.getString(0).split("\t");
                    for(String genre : genreList){
                        int genreIndex = genreStats[0].indexOf(genre);
                        if (genreIndex < 0){
                            genreStats[0].add(genre);
                            genreStats[1].add(cr.getLong(1));
                        }else{
                            genreStats[1].set(genreIndex, (((ArrayList<Long>)genreStats[1]).get(genreIndex)+ cr.getLong(1)));
                        }
                    }

                    cr.moveToNext();
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieCollectionCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }
        return genreStats;
    }

    public ArrayList[] getMovieCollectionFilmRatingCount(){
        ArrayList[] ratingStats = new ArrayList[2];
        ratingStats[0] = new ArrayList<String>();
        ratingStats[1] = new ArrayList<Long>();

        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        Log.i("Movie Metric" , "Collection rating Count" );
        try {
            cr = sq.rawQuery("SELECT "+ MovieTable.FILM_RATING + ", COUNT( " + MovieTable.FILM_RATING +" ) FROM "
                    + MovieTable.TABLE_NAME + " WHERE " +  MovieTable.IS_COLLECTED + " = 1 "
                    + "GROUP BY " + MovieTable.FILM_RATING , null);
            if (cr.moveToFirst()) {
                while (!cr.isAfterLast()) {
                    Log.i("Movie Metric" , cr.getString(0) + " : " + cr.getLong(1) );
                    ratingStats[0].add(cr.getString(0));
                    ratingStats[1].add(cr.getLong(1));
                    cr.moveToNext();
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieCollectionCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return ratingStats;
    }

    //TODO:FINISH THIS
    public ArrayList[] getMovieViewingGenreCount(){
        ArrayList[] genreStats = new ArrayList[2];
        genreStats[0] = new ArrayList<String>();
        genreStats[1] = new ArrayList<Long>();

        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();

        Log.i("Movie Metric", "Viewing Film Rating");
        try {
            cr = sq.rawQuery("SELECT "+ MovieTable.GENRE + ", COUNT( " + MovieTable.GENRE +" ) FROM "
                    + MovieTable.TABLE_NAME + " WHERE " +  MovieTable.WATCH_COUNT + " > 0 "
                    + "GROUP BY " + MovieTable.GENRE , null);
            if (cr.moveToFirst()) {
                while (!cr.isAfterLast()) {
                    Log.i("Movie Metric" , cr.getString(0) + " : " + cr.getLong(1) );
                    String[] genreList = cr.getString(0).split("\t");
                    for(String genre : genreList){
                        int genreIndex = genreStats[0].indexOf(genre);
                        if (genreIndex < 0){
                            genreStats[0].add(genre);
                            genreStats[1].add(cr.getLong(1));
                        } else {
                            genreStats[1].set(genreIndex, (((ArrayList<Long>)genreStats[1]).get(genreIndex)+ cr.getLong(1)));
                        }
                    }
                    cr.moveToNext();
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieCollectionCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return genreStats;
    }

    public ArrayList[] getMovieViewingFilmRatingCount(){
        ArrayList[] ratingStats = new ArrayList[2];
        ratingStats[0] = new ArrayList<String>();
        ratingStats[1] = new ArrayList<Long>();
        Log.i("Movie Metric" , "Viewing Film Rating" );
        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        try {
            cr = sq.rawQuery("SELECT "+ MovieTable.FILM_RATING + ", SUM( " + MovieTable.WATCH_COUNT
                    + " ) FROM " + MovieTable.TABLE_NAME + " WHERE " +  MovieTable.WATCH_COUNT
                    + " > 0 " + "GROUP BY " + MovieTable.FILM_RATING, null);
            if (cr.moveToFirst()) {
                while (!cr.isAfterLast()) {
                    Log.i("Movie Metric" , cr.getString(0) + " : " + cr.getLong(1) );
                    ratingStats[0].add(cr.getString(0));
                    ratingStats[1].add(cr.getLong(1));
                    cr.moveToNext();
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieCollectionCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }


        return ratingStats;
    }

    public ArrayList[] getMovieCollectionMyRatingsCount(){
        ArrayList[] myRatingStats = new ArrayList[2];
        myRatingStats[0] = new ArrayList<String>();
        myRatingStats[1] = new ArrayList<Long>();

        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        try {
            cr = sq.rawQuery("SELECT "+ MovieTable.MY_RATING + ", COUNT( " + MovieTable.MY_RATING
                    + " ) FROM " + MovieTable.TABLE_NAME + " WHERE " +  MovieTable.MY_RATING
                    + " > 0 OR " + MovieTable.MY_REVIEW + " NOT NULL "
                    + "GROUP BY " + MovieTable.MY_RATING , null);
            if (cr.moveToFirst()) {
                while (!cr.isAfterLast()) {
                    Log.i("Movie Metric" , cr.getString(0) + " : " + cr.getLong(1) );
                    myRatingStats[0].add(cr.getString(0));
                    myRatingStats[1].add(cr.getLong(1));
                    cr.moveToNext();
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMovieCollectionCount() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return myRatingStats;
    }

    // Gets minimum (furthest back) release date of movies in collection
    public String getMinReleaseDate() {
        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        String strDate = "";
        try {
            cr = sq.rawQuery("SELECT MIN(" + MovieTable.RELEASE_DATE + ") FROM "
                    + MovieTable.TABLE_NAME + " WHERE " + MovieTable.IS_COLLECTED + " = 1", null);
            if (cr.moveToFirst()) {
                if (!cr.isAfterLast()) {
                    strDate = cr.getString(0);
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "getMinReleaseData() error", e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return strDate;
    }

    // Gets movies recently added to collection
    public ArrayList<Movie> getRecentlyAdded() {
        String query = "SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE "
                       + MovieTable.IS_COLLECTED + " = 1 ORDER BY " + MovieTable.DATE_ADDED
                       + " DESC";

        return movieListQuery(query);
    }

    // Gets movies recently added to collection
    public ArrayList<Movie> getJustWatched() {
        String query = "SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE "
                + MovieTable.WATCH_COUNT + " > 0 ORDER BY " + MovieTable.LAST_WATCHED
                + " DESC";

        return movieListQuery(query);
    }

    // Gets all rated movies in collection, sorted by title (default collection filter)
    public ArrayList<Movie> getRatedMovies() {
        String query = "SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE "
                + MovieTable.MY_RATING + " > 0 OR " + MovieTable.MY_REVIEW
                + " NOT NULL ORDER BY " + MovieTable.TITLE + " ASC";

        Log.d(TAG, "getRatedMovies() called");

        return movieListQuery(query);
    }

    /**
     * gets movies in the collection based on a filter object
     * @param filter
     * @return list of movies that fit query
     */
    public ArrayList<Movie> getMovieByFilter(MovieFilter filter){
        String query = buildQueryFromFilter(filter);
        Log.d( "Filter Query", query );
        return movieListQuery(query);
    }

    /**
     * Helper function that builds a raw query from a filter object
     * @param filter
     * @return raw sql query string
     */
    private String buildQueryFromFilter(MovieFilter filter){
        String query = "";
        boolean addAndToQuery = false;
        query += "SELECT * FROM " + MovieDbContract.MovieTable.TABLE_NAME + " ";
        query += "WHERE ";

        if(!filter.getMaxRating().equals(MovieDbHelper.NO_PREF)){
            query += MovieDbContract.MovieTable.FILM_RATING + " IN  ( ";
            switch (filter.getMaxRating()) {
                case "N-17":
                    query += "\"NC-17\", ";
                case "R":
                    query += "\"R\", ";
                case "PG-13":
                    query += "\"PG-13\", ";
                case "PG":
                    query += "\"PG\", ";
                case "G":
                    query += "\"G\" ) ";
                    break;
                default:
                    query += ") ";
                    break;
            }
            addAndToQuery = true;
        }

        if (addAndToQuery){
            query += "AND ";
            addAndToQuery = false;
        }

        if ( !filter.getGenres().equals(MovieDbHelper.NO_PREF) ){
            query += filter.getGenres();
            addAndToQuery = true;
        }

        if (addAndToQuery){
            query += "AND ";
            addAndToQuery = false;
        }

        if(filter.getMaxRuntime() < filter.MAX_RUNTIME) {
            query += MovieDbContract.MovieTable.RUNTIME + " <= " + filter.getMaxRuntime() + " ";
            addAndToQuery = true;
        }

        if (addAndToQuery){
            query += "AND ";
            addAndToQuery = false;
        }

        query += MovieDbContract.MovieTable.RELEASE_DATE + " BETWEEN '" + filter.getMinDate() + "'"
                + " AND '" + filter.getMaxDate() + "' ";

        query += "AND " + MovieDbContract.MovieTable.IS_COLLECTED + " = 1 ";

        query += "ORDER BY " + filter.getSort() + " " + filter.isAscendingString();

        return query;
    }


    // Helper function for retrieving movie lists from local Db
    private ArrayList<Movie> movieListQuery(String query) {
        ArrayList<Movie> movies = new ArrayList<>();
        Cursor cr = null;
        SQLiteDatabase sq = this.getReadableDatabase();
        try {
            cr = sq.rawQuery(query, null);
            if (cr.moveToFirst()) {
                while (!cr.isAfterLast()) {
                    movies.add(new Movie(
                                    cr.getInt(cr.getColumnIndex(MovieTable.MOVIE_ID)),
                                    cr.getString(cr.getColumnIndex(MovieTable.TITLE)),
                                    cr.getString(cr.getColumnIndex(MovieTable.RELEASE_DATE)),
                                    cr.getString(cr.getColumnIndex(MovieTable.FILM_RATING)),
                                    cr.getInt(cr.getColumnIndex(MovieTable.RUNTIME)),
                                    cr.getDouble(cr.getColumnIndex(MovieTable.VOTE_AVERAGE)),
                                    cr.getInt(cr.getColumnIndex(MovieTable.VOTE_COUNT)),
                                    cr.getString(cr.getColumnIndex(MovieTable.TAG_LINE)),
                                    cr.getString(cr.getColumnIndex(MovieTable.SYNOPSIS)),
                                    cr.getString(cr.getColumnIndex(MovieTable.POSTER_URL)),
                                    cr.getString(cr.getColumnIndex(MovieTable.GENRE)),
                                    cr.getString(cr.getColumnIndex(MovieTable.DIRECTOR)),
                                    cr.getString(cr.getColumnIndex(MovieTable.STUDIO)),
                                    cr.getDouble(cr.getColumnIndex(MovieTable.POPULARITY)),
                                    cr.getLong(cr.getColumnIndex(MovieTable.BUDGET)),
                                    cr.getLong(cr.getColumnIndex(MovieTable.REVENUE)),
                                    cr.getDouble(cr.getColumnIndex(MovieTable.MY_RATING)),
                                    cr.getString(cr.getColumnIndex(MovieTable.MY_REVIEW)),
                                    cr.getString(cr.getColumnIndex(MovieTable.LAST_WATCHED)),
                                    cr.getInt(cr.getColumnIndex(MovieTable.WATCH_COUNT)),
                                    cr.getInt(cr.getColumnIndex(MovieTable.IS_LOANED)),
                                    cr.getString(cr.getColumnIndex(MovieTable.DATE_ADDED)),
                                    cr.getInt(cr.getColumnIndex(MovieTable.IS_COLLECTED)))
                    );
                    cr.moveToNext();
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Query: " + query, e);
        } finally {
            if (cr != null) { cr.close(); }
            if (sq != null) { sq.close(); }
        }

        return movies;
    }

    // Helper function for making changes to local Db
    private void updateDb(String command) {
        SQLiteDatabase sq = this.getWritableDatabase();
        try {
            sq.execSQL(command);
        } catch (SQLiteException e) {
            Log.e(TAG, command, e);
        } finally {
            if (sq != null) { sq.close(); }
        }
    }
}
