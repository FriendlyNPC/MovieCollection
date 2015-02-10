package com.school.comp3717.moviecollection;

import android.provider.BaseColumns;

public final class MovieDbContract {

    public MovieDbContract() {}

    public static abstract class MovieTable implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String MOVIE_ID = "movieId";
        public static final String TITLE = "title";
        public static final String YEAR = "year";
        public static final String MPAA_RATING = "mpaaRating";
        public static final String RUNTIME = "runtime";
        public static final String CRITIC_SCORE = "criticScore";
        public static final String USER_SCORE = "userScore";
        public static final String SYNOPSIS = "synopsis";
        public static final String POSTER_URL = "posterUrl";
        public static final String GENRE = "genre";
        public static final String DIRECTOR = "director";
        public static final String STUDIO = "studio";
        public static final String LAST_WATCHED = "lastWatched";
        public static final String WATCH_COUNT = "watchCount";
        public static final String IS_LOANED = "isLoaned";
    }

    // public abstract class GenreTable implements BaseColumns {}

}
