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
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If DB schema changed, must increment DB version; otherwise, DB errors
    private static final int              DATABASE_VERSION = 7;
    private static final String           DATABASE_NAME    = "Movie.db";
    private static final SimpleDateFormat DATE_FORMAT      = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String           TAG              = "MovieDbHelper";
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
        Log.d(TAG, "Movie database created");
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
     * DB version to be incremented
     */
    public void cleanDatabase() {
        SQLiteDatabase sq = this.getWritableDatabase();
        sq.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sq);
    }

    // Adds movie to local DB
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
        SQLiteDatabase sq = this.getWritableDatabase();
        String setCollectionCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.IS_COLLECTED
                                  + " = " + "1 WHERE " + MovieTable.MOVIE_ID + " = " + movie.getMovieId();
        String setDateAddedCmd  = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.DATE_ADDED
                                  + " = \"" + date + "\" WHERE " + MovieTable.MOVIE_ID + " = " + movie.getMovieId();
        sq.execSQL(setCollectionCmd);
        sq.execSQL(setDateAddedCmd);
        sq.close();
        Log.d(TAG, "Movie added to collection");
    }

    // Removes movie from Movie table
    public void removeMovieByID(int movieId) {
        SQLiteDatabase sq = this.getWritableDatabase();
        String removeMovieByIDCommand = "DELETE FROM " + MovieTable.TABLE_NAME + " WHERE " +
                                        MovieTable.MOVIE_ID + " = " + movieId;
        sq.execSQL(removeMovieByIDCommand);
        sq.close();
        Log.d(TAG, "Movie removed from Movie table");
    }

    // Removes movie from collection but remains in Movie table (for watch count, review, rating, etc.)
    public void removeMovieFromCollection(int movieId) {
        SQLiteDatabase sq = this.getWritableDatabase();
        String setCollectionCmd   = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.IS_COLLECTED
                                    + " = " + "0 WHERE " + MovieTable.MOVIE_ID + " = " + movieId;
        String removeDateAddedCmd = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.DATE_ADDED
                                    + " = NULL WHERE " + MovieTable.MOVIE_ID + " = " + movieId;
        sq.execSQL(setCollectionCmd);
        sq.execSQL(removeDateAddedCmd);
        sq.close();
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
            movie = new Movie(cr.getInt(    cr.getColumnIndex( MovieTable.MOVIE_ID )),
                              cr.getString( cr.getColumnIndex( MovieTable.TITLE )),
                              cr.getString( cr.getColumnIndex( MovieTable.RELEASE_DATE )),
                              cr.getString( cr.getColumnIndex( MovieTable.FILM_RATING )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.RUNTIME )),
                              cr.getDouble( cr.getColumnIndex( MovieTable.VOTE_AVERAGE )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.VOTE_COUNT )),
                              cr.getString( cr.getColumnIndex( MovieTable.TAG_LINE )),
                              cr.getString( cr.getColumnIndex( MovieTable.SYNOPSIS )),
                              cr.getString( cr.getColumnIndex( MovieTable.POSTER_URL )),
                              cr.getString( cr.getColumnIndex( MovieTable.GENRE )),
                              cr.getString( cr.getColumnIndex( MovieTable.DIRECTOR )),
                              cr.getString( cr.getColumnIndex( MovieTable.STUDIO )),
                              cr.getDouble( cr.getColumnIndex( MovieTable.POPULARITY )),
                              cr.getLong(   cr.getColumnIndex( MovieTable.BUDGET )),
                              cr.getLong(   cr.getColumnIndex( MovieTable.REVENUE )),
                              cr.getDouble( cr.getColumnIndex( MovieTable.MY_RATING )),
                              cr.getString( cr.getColumnIndex( MovieTable.MY_REVIEW )),
                              cr.getString( cr.getColumnIndex( MovieTable.LAST_WATCHED )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.WATCH_COUNT )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.IS_LOANED )),
                              cr.getString( cr.getColumnIndex( MovieTable.DATE_ADDED )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.IS_COLLECTED )));
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
    public List<Integer> getMovieIDsExist(List<MovieDb> toSearch) {
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
                        new Movie(
                                cr.getInt(    cr.getColumnIndex( MovieTable.MOVIE_ID )),
                                cr.getString( cr.getColumnIndex( MovieTable.TITLE )),
                                cr.getString( cr.getColumnIndex( MovieTable.RELEASE_DATE )),
                                cr.getString( cr.getColumnIndex( MovieTable.FILM_RATING )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.RUNTIME )),
                                cr.getDouble( cr.getColumnIndex( MovieTable.VOTE_AVERAGE )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.VOTE_COUNT )),
                                cr.getString( cr.getColumnIndex( MovieTable.TAG_LINE )),
                                cr.getString( cr.getColumnIndex( MovieTable.SYNOPSIS )),
                                cr.getString( cr.getColumnIndex( MovieTable.POSTER_URL )),
                                cr.getString( cr.getColumnIndex( MovieTable.GENRE )),
                                cr.getString( cr.getColumnIndex( MovieTable.DIRECTOR )),
                                cr.getString( cr.getColumnIndex( MovieTable.STUDIO )),
                                cr.getDouble( cr.getColumnIndex( MovieTable.POPULARITY )),
                                cr.getLong(   cr.getColumnIndex( MovieTable.BUDGET )),
                                cr.getLong(   cr.getColumnIndex( MovieTable.REVENUE )),
                                cr.getDouble( cr.getColumnIndex( MovieTable.MY_RATING )),
                                cr.getString( cr.getColumnIndex( MovieTable.MY_REVIEW )),
                                cr.getString( cr.getColumnIndex( MovieTable.LAST_WATCHED )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.WATCH_COUNT )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.IS_LOANED )),
                                cr.getString( cr.getColumnIndex( MovieTable.DATE_ADDED )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.IS_COLLECTED )))
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
                    new Movie(cr.getInt(    cr.getColumnIndex( MovieTable.MOVIE_ID )),
                              cr.getString( cr.getColumnIndex( MovieTable.TITLE )),
                              cr.getString( cr.getColumnIndex( MovieTable.RELEASE_DATE )),
                              cr.getString( cr.getColumnIndex( MovieTable.FILM_RATING )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.RUNTIME )),
                              cr.getDouble( cr.getColumnIndex( MovieTable.VOTE_AVERAGE )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.VOTE_COUNT )),
                              cr.getString( cr.getColumnIndex( MovieTable.TAG_LINE )),
                              cr.getString( cr.getColumnIndex( MovieTable.SYNOPSIS )),
                              cr.getString( cr.getColumnIndex( MovieTable.POSTER_URL )),
                              cr.getString( cr.getColumnIndex( MovieTable.GENRE )),
                              cr.getString( cr.getColumnIndex( MovieTable.DIRECTOR )),
                              cr.getString( cr.getColumnIndex( MovieTable.STUDIO )),
                              cr.getDouble( cr.getColumnIndex( MovieTable.POPULARITY )),
                              cr.getLong(   cr.getColumnIndex( MovieTable.BUDGET )),
                              cr.getLong(   cr.getColumnIndex( MovieTable.REVENUE )),
                              cr.getDouble( cr.getColumnIndex( MovieTable.MY_RATING )),
                              cr.getString( cr.getColumnIndex( MovieTable.MY_REVIEW )),
                              cr.getString( cr.getColumnIndex( MovieTable.LAST_WATCHED )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.WATCH_COUNT )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.IS_LOANED )),
                              cr.getString( cr.getColumnIndex( MovieTable.DATE_ADDED )),
                              cr.getInt(    cr.getColumnIndex( MovieTable.IS_COLLECTED )))
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
        String watchCountCmd  = "UPDATE " + MovieTable.TABLE_NAME + " SET " + MovieTable.WATCH_COUNT
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

    public List<String> getAllChoices(String column) {
        List<String> choices = new ArrayList<>();
        Set<String> tempSet = new TreeSet<>();

        // Select all query
        String selectQuery = "SELECT DISTINCT " + column + " FROM " + MovieTable.TABLE_NAME;

        SQLiteDatabase sq = this.getReadableDatabase();
        Cursor cr = sq.rawQuery(selectQuery, null);

        // Loop through all rows, parse them, and add to set (remove duplicates)
        if (cr.moveToFirst()) {
            while (!cr.isAfterLast()) {
                String temp = cr.getString(0);
                StringTokenizer st = new StringTokenizer(temp, "\t");
                while(st.hasMoreTokens())
                    tempSet.add(st.nextToken());
                cr.moveToNext();
            }
        }

        for (String item : tempSet) {
            choices.add(item);
        }

        cr.close();
        sq.close();

        return choices;
    }

    // Gets random picks from movie collection in local DB using filters provided
    public ArrayList<Movie> getRandomPicks(String genre,
                                      String filmRating,
                                      int runtime,
                                      boolean isUnwatched,
                                      String minReleaseDate,
                                      String maxReleaseDate) {
        String query;
        Cursor cr;
        ArrayList<Movie> movies = new ArrayList<>();
        SQLiteDatabase sq = this.getReadableDatabase();
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
        }

        if (ratingQuery.equals(MovieTable.FILM_RATING + " IN (")) {
            ratingQuery = "";
        }

        query = "SELECT * FROM " + MovieTable.TABLE_NAME + " WHERE "
                + MovieTable.GENRE + " LIKE \"%" + genre + "%\" AND "
                + ratingQuery + MovieTable.RUNTIME + " <= " + runtime + " AND "
                + MovieTable.RELEASE_DATE + " BETWEEN \"" + minReleaseDate + "\" AND \""
                + maxReleaseDate + "\" AND " + MovieTable.IS_COLLECTED + " = 1";
        if (isUnwatched) {
            query += " AND " + MovieTable.WATCH_COUNT + " = 0";
        }
        cr = sq.rawQuery(query, null);

        if (cr.moveToFirst()) {
            while (!cr.isAfterLast()) {
                movies.add(
                      new Movie(cr.getInt(    cr.getColumnIndex( MovieTable.MOVIE_ID )),
                                cr.getString( cr.getColumnIndex( MovieTable.TITLE )),
                                cr.getString( cr.getColumnIndex( MovieTable.RELEASE_DATE )),
                                cr.getString( cr.getColumnIndex( MovieTable.FILM_RATING )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.RUNTIME )),
                                cr.getDouble( cr.getColumnIndex( MovieTable.VOTE_AVERAGE )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.VOTE_COUNT )),
                                cr.getString( cr.getColumnIndex( MovieTable.TAG_LINE )),
                                cr.getString( cr.getColumnIndex( MovieTable.SYNOPSIS )),
                                cr.getString( cr.getColumnIndex( MovieTable.POSTER_URL )),
                                cr.getString( cr.getColumnIndex( MovieTable.GENRE )),
                                cr.getString( cr.getColumnIndex( MovieTable.DIRECTOR )),
                                cr.getString( cr.getColumnIndex( MovieTable.STUDIO )),
                                cr.getDouble( cr.getColumnIndex( MovieTable.POPULARITY )),
                                cr.getLong(   cr.getColumnIndex( MovieTable.BUDGET )),
                                cr.getLong(   cr.getColumnIndex( MovieTable.REVENUE )),
                                cr.getDouble( cr.getColumnIndex( MovieTable.MY_RATING )),
                                cr.getString( cr.getColumnIndex( MovieTable.MY_REVIEW )),
                                cr.getString( cr.getColumnIndex( MovieTable.LAST_WATCHED )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.WATCH_COUNT )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.IS_LOANED )),
                                cr.getString( cr.getColumnIndex( MovieTable.DATE_ADDED )),
                                cr.getInt(    cr.getColumnIndex( MovieTable.IS_COLLECTED )))
                );
                cr.moveToNext();
            }
        }
        cr.close();
        sq.close();

        return movies;
    }

    public String getMinReleaseDate() {
        Cursor cr;
        SQLiteDatabase sq = this.getReadableDatabase();
        cr = sq.rawQuery("SELECT MIN(" + MovieTable.RELEASE_DATE + ") FROM "
                         + MovieTable.TABLE_NAME + " WHERE " + MovieTable.IS_COLLECTED + " = 1", null);
        String strDate = "";
        if (cr.moveToFirst()) {
            if (!cr.isAfterLast()) {
                strDate = cr.getString(0);
            }
        }
        cr.close();
        sq.close();

        return strDate;
    }
}
