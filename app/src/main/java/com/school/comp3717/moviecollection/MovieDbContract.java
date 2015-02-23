package com.school.comp3717.moviecollection;

import android.provider.BaseColumns;

public final class MovieDbContract {

    public MovieDbContract() {}

    public static abstract class MovieTable implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String MOVIE_ID = "movieId";
        public static final String TITLE = "title";
        public static final String RELEASE_DATE = "releaseDate";
        public static final String FILM_RATING = "filmRating";
        public static final String RUNTIME = "runtime";
        public static final String VOTE_AVERAGE = "voteAverage";
        public static final String VOTE_COUNT = "voteCount";
        public static final String TAG_LINE = "tagLine";
        public static final String SYNOPSIS = "synopsis";
        public static final String POSTER_URL = "posterUrl";
        public static final String GENRE = "genre";
        public static final String DIRECTOR = "director";
        public static final String STUDIO = "studio";
        public static final String POPULARITY = "popularity";
        public static final String BUDGET = "budget";
        public static final String REVENUE = "revenue";

        public static final String MY_RATING = "myRating";
        public static final String MY_REVIEW = "myReview";
        public static final String LAST_WATCHED = "lastWatched";
        public static final String WATCH_COUNT = "watchCount";
        public static final String IS_LOANED = "isLoaned";
        public static final String DATE_ADDED = "dateAdded";
        public static final String IS_COLLECTED = "isCollected";
    }

    // public abstract class GenreTable implements BaseColumns {}

}
