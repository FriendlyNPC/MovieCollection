package com.school.comp3717.moviecollection;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.school.comp3717.moviecollection.tools.DownloadPosterTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetails extends Fragment {

    private static final DecimalFormat RATING_FORMAT = new DecimalFormat("0.#");
    private static final NumberFormat  DOLLAR_FORMAT = NumberFormat.getCurrencyInstance();
    private static final int           YEAR_LENGTH   = 4;
    private static final int           DATE_LENGTH   = 10;

    Movie     movie;
    TextView  title;
    TextView  tagLine;
    TextView  synopsis;
    TextView  director;
    TextView  userRating;
    TextView  releaseDate;
    TextView  filmRating;
    TextView  runtime;
    TextView  genre;
    TextView  studio;
    TextView  budget;
    TextView  revenue;
    TextView  lastWatched;
    TextView  watchCount;
    RatingBar userRatingBar;
    RatingBar myRatingBar;
    ImageView poster;
    EditText  myReview;
    Button    watchBtn;
    Button    addRemoveBtn;
    Button    reviewBtn;

    public MovieDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        poster        = (ImageView) rootView.findViewById(R.id.moviePoster);
        title         = (TextView)  rootView.findViewById(R.id.movieTitle);
        tagLine       = (TextView)  rootView.findViewById(R.id.tagLine);
        synopsis      = (TextView)  rootView.findViewById(R.id.synopsisBody);
        director      = (TextView)  rootView.findViewById(R.id.movieDirector);
        userRating    = (TextView)  rootView.findViewById(R.id.userRatingText);
        userRatingBar = (RatingBar) rootView.findViewById(R.id.userRatingBar);
        myRatingBar   = (RatingBar) rootView.findViewById(R.id.myRatingBar);
        myReview      = (EditText)  rootView.findViewById(R.id.myReview);
        releaseDate   = (TextView)  rootView.findViewById(R.id.releaseDateField);
        filmRating    = (TextView)  rootView.findViewById(R.id.filmRatingField);
        runtime       = (TextView)  rootView.findViewById(R.id.runtimeField);
        genre         = (TextView)  rootView.findViewById(R.id.genreField);
        studio        = (TextView)  rootView.findViewById(R.id.studioField);
        budget        = (TextView)  rootView.findViewById(R.id.budgetField);
        revenue       = (TextView)  rootView.findViewById(R.id.revenueField);
        lastWatched   = (TextView)  rootView.findViewById(R.id.lastWatchedField);
        watchCount    = (TextView)  rootView.findViewById(R.id.watchCountField);
        watchBtn      = (Button)    rootView.findViewById(R.id.watchMovieButton);
        addRemoveBtn  = (Button)    rootView.findViewById(R.id.addMovieButton);
        reviewBtn     = (Button)    rootView.findViewById(R.id.saveReviewButton);

        movie = this.getArguments().getParcelable("movie");
        setDetails();

        if (movie.getPosterUrl() != null) {
            new DownloadPosterTask(getActivity(), (ImageView) rootView.findViewById(R.id.moviePoster))
                    .execute("http://image.tmdb.org/t/p/w185" + movie.getPosterUrl());
        }

        return rootView;
    }

    private void setDetails() {
        setTitleText(movie, title);
        setDirectorText(movie, director);
        setTagLineText(movie, tagLine);
        setSynopsisText(movie, synopsis);
        synopsis.setText(movie.getSynopsis());
        userRating.setText("User Rating:\n" + RATING_FORMAT.format(movie.getVoteAverage())
                           + "/5 (" + movie.getVoteCount() + " votes)");
        userRatingBar.setRating((float)movie.getVoteAverage());
        // If myRating < 0, means user has not rated it yet; show rating bar as 0 out of 5
        myRatingBar.setRating((movie.getMyRating() < 0) ? 0 : (float)movie.getMyRating());
        myReview.setText(movie.getMyReview());

        setFieldFromText(movie.getReleaseDate(), releaseDate);
        setFieldFromText(movie.getFilmRating(), filmRating);
        setRuntimeText(movie, runtime);
        setFieldFromArray(movie.getGenre(), genre);
        setFieldFromArray(movie.getStudio(), studio);
        setDollarText(movie.getBudget(), budget);
        setDollarText(movie.getRevenue(), revenue);
        setWatchFields(movie, lastWatched, watchCount);

        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
        boolean currentState;
        if (dbHelper.getMovieById(movie.getMovieId()) == null ||
            dbHelper.getMovieById(movie.getMovieId()).isCollected() == 0) {
            addRemoveBtn.setText(R.string.add_movie_text);
            addRemoveBtn.setBackgroundColor(getResources().getColor(R.color.green_300));
            currentState = true;
        } else {
            addRemoveBtn.setText(R.string.remove_movie_text);
            addRemoveBtn.setBackgroundColor(getResources().getColor(R.color.red_800));
            currentState = false;
        }

        addRemoveBtn.setOnClickListener(
                new AddButtonOnClickListener(getActivity(), addRemoveBtn, currentState));
        watchBtn.setOnClickListener(
                new WatchButtonOnClickListener(getActivity(), watchBtn));
        myRatingBar.setOnRatingBarChangeListener(
                new RatingBarOnChangeListener(getActivity(), myRatingBar));
        reviewBtn.setOnClickListener(
                new ReviewButtonOnClickListener(getActivity(), reviewBtn, myReview));
    }

    private void setDollarText(long value, TextView text) {
        String dollarValue;
        int centsIndex;
        if (value > 0) {
            dollarValue = "$" + DOLLAR_FORMAT.format(value);
            if (dollarValue.endsWith(".00")) {
                centsIndex = dollarValue.lastIndexOf(".00");
                if (centsIndex != -1) {
                    dollarValue = dollarValue.substring(1, centsIndex);
                }
            }
            text.setText(dollarValue);
        }
    }

    private void setRuntimeText(Movie movie, TextView runtime) {
        String runtimeStr = Integer.toString(movie.getRuntime());
        if (runtimeStr != null && !runtimeStr.isEmpty() && !runtimeStr.equals("0")) {
            runtime.setText(runtimeStr + " minutes");
        }
    }

    private void setDirectorText(Movie movie, TextView director) {
        String directedBy = "Directed by";
        String[] directorArray = movie.getDirector().split("\t");
        int numDirectors = 0;
        for (String directorName : directorArray) {
            directedBy += (numDirectors == 0) ? " " : ", ";
            directedBy += directorName;
            numDirectors++;
        }
        directedBy = directedBy.trim();
        if (directedBy.equals("Directed by")) {
            director.setVisibility(View.GONE);
        } else {
            director.setVisibility(View.VISIBLE);
            director.setText(directedBy);
        }
    }

    private void setFieldFromArray(String tabbedText, TextView field) {
        String fieldStr = "";
        String[] fieldArray = tabbedText.split("\t");
        int num = 0;
        for (String fieldName : fieldArray) {
            fieldStr += (num == 0) ? "" : ",\n";
            fieldStr += fieldName;
            num++;
        }
        fieldStr = fieldStr.trim();
        if (!fieldStr.isEmpty()) {
            field.setText(fieldStr);
        }
    }

    private void setTagLineText(Movie movie, TextView tagLine) {
        String tagLineStr = movie.getTagLine();
        if (tagLineStr == null || tagLineStr.isEmpty()) {
            tagLine.setVisibility(View.GONE);
        } else {
            tagLine.setVisibility(View.VISIBLE);
            tagLine.setText("\"" + tagLineStr + "\"");
        }
    }

    private void setSynopsisText(Movie movie, TextView synopsis) {
        String synopsisStr = movie.getSynopsis();
        if (synopsisStr == null || synopsisStr.isEmpty()) {
            synopsis.setVisibility(View.GONE);
        } else {
            synopsis.setVisibility(View.VISIBLE);
            synopsis.setText(synopsisStr);
        }
    }

    private void setTitleText(Movie movie, TextView movieTitle) {
        String title = movie.getTitle();
        String year;
        if (movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
            year = " (" + movie.getReleaseDate().substring(0, YEAR_LENGTH) + ")";
            title += year;
        }
        movieTitle.setText(title);
    }

    private void setFieldFromText(String str, TextView field) {
        if (str != null && !str.isEmpty()) {
            field.setText(str);
        }
    }

    private void setWatchFields(Movie movie, TextView lastWatched, TextView watchCount) {
        String count = Integer.toString(movie.getWatchCount());
        String watched;
        if (movie.getLastWatched() != null && !movie.getLastWatched().isEmpty()) {
            watched = movie.getLastWatched().substring(0, DATE_LENGTH);
            lastWatched.setText(watched);
            watchCount.setText(count);
        }
    }

    private class AddButtonOnClickListener implements View.OnClickListener {
        Context context;
        Button addOrRemoveButton;
        boolean showingAdd;

        public AddButtonOnClickListener(Context context, Button addOrRemoveButton, boolean showAdd) {
            super();
            this.context = context;
            this.addOrRemoveButton = addOrRemoveButton;
            this.showingAdd = showAdd;
        }

        @Override
        public void onClick(View view) {
            addOrRemoveButton.setEnabled(false);
            MovieDbHelper dbHelper = new MovieDbHelper(getActivity());

            if (showingAdd) {
                dbHelper.addMovieToCollection(movie);
                addOrRemoveButton.setText(R.string.remove_movie_text);
                addOrRemoveButton.setBackgroundColor(getResources().getColor(R.color.red_800));
                Toast.makeText(context, movie.getTitle() + " added to collection.", Toast.LENGTH_LONG).show();
            } else {
                dbHelper.removeMovieFromCollection(movie.getMovieId());
                addOrRemoveButton.setText(R.string.add_movie_text);
                addOrRemoveButton.setBackgroundColor(getResources().getColor(R.color.green_300));
                Toast.makeText(context, movie.getTitle() + " removed from collection.", Toast.LENGTH_LONG).show();
            }

            showingAdd = !showingAdd;
            addOrRemoveButton.setEnabled(true);

        }
    }

    private class WatchButtonOnClickListener implements View.OnClickListener {
        Context context;
        Button watchButton;

        public WatchButtonOnClickListener(Context context, Button watchButton) {
            super();
            this.context = context;
            this.watchButton = watchButton;
        }

        @Override
        public void onClick(View view) {
            watchButton.setEnabled(false);
            MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
            dbHelper.addMovie(movie);
            dbHelper.updateWatchCount(movie);
            String times = (movie.getWatchCount() > 1) ? " times." : " time.";
            Toast.makeText(context, movie.getTitle() + " has been watched "
                           + movie.getWatchCount() + times, Toast.LENGTH_SHORT).show();
            setWatchFields(movie, lastWatched, watchCount);
            watchButton.setEnabled(true);
        }
    }

    private class RatingBarOnChangeListener implements RatingBar.OnRatingBarChangeListener {
        Context context;
        RatingBar myRating;

        public RatingBarOnChangeListener(Context context, RatingBar myRating) {
            super();
            this.context = context;
            this.myRating = myRating;
        }

        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean userTouch) {
            myRating.setEnabled(false);
            MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
            dbHelper.addMovie(movie);
            dbHelper.updateMyRating(movie, rating);
            Toast.makeText(context, movie.getTitle() + " has been rated "
                    + movie.getMyRating() + " out of 5", Toast.LENGTH_SHORT).show();
            myRating.setEnabled(true);
        }
    }

    private class ReviewButtonOnClickListener implements View.OnClickListener {
        Context context;
        Button reviewButton;
        EditText review;

        public ReviewButtonOnClickListener(Context context, Button reviewButton, EditText review) {
            super();
            this.context = context;
            this.reviewButton = reviewButton;
            this.review = review;
        }

        @Override
        public void onClick(View view) {
            String reviewText = review.getText().toString();
            reviewButton.setEnabled(false);
            MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
            dbHelper.addMovie(movie);
            dbHelper.updateMyReview(movie, reviewText);
            Toast.makeText(context, "Your review has been saved.", Toast.LENGTH_SHORT).show();
            reviewButton.setEnabled(true);
        }
    }
}
