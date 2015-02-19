package com.school.comp3717.moviecollection;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetails extends Fragment {

    private static final DecimalFormat RATING_FORMAT = new DecimalFormat("0.#");
    private static final int YEAR_LENGTH = 4;

    Movie movie;
    TextView title;
    TextView year;
    TextView tagLine;
    TextView synopsis;
    TextView director;
    TextView userRating;
    RatingBar userRatingBar;
    RatingBar myRatingBar;
    ImageView poster;
    EditText myReview;

    public MovieDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        poster = (ImageView) rootView.findViewById(R.id.moviePoster);
        title = (TextView) rootView.findViewById(R.id.movieTitle);
        year = (TextView) rootView.findViewById(R.id.movieYear);
        tagLine = (TextView) rootView.findViewById(R.id.tagLine);
        synopsis = (TextView) rootView.findViewById(R.id.synopsisBody);
        director = (TextView) rootView.findViewById(R.id.movieDirector);
        userRating = (TextView) rootView.findViewById(R.id.userRatingText);
        userRatingBar = (RatingBar) rootView.findViewById(R.id.userRatingBar);
        myRatingBar = (RatingBar) rootView.findViewById(R.id.myRatingBar);
        myReview = (EditText) rootView.findViewById(R.id.myReview);

        movie = this.getArguments().getParcelable("movie");
        setDetails();

        if (movie.getPosterUrl() != null) {
            new DownloadPosterTask((ImageView) rootView.findViewById(R.id.moviePoster))
                    .execute("http://image.tmdb.org/t/p/w185" + movie.getPosterUrl());
        }

        return rootView;
    }

    private void setDetails() {
        title.setText(movie.getTitle());
        setYearText(movie, year);
        setDirectorText(movie, director);
        setTagLineText(movie, tagLine);
        setSynopsisText(movie, synopsis);
        synopsis.setText(movie.getSynopsis());
        userRating.setText("User Rating:\n" + RATING_FORMAT.format(movie.getVoteAverage())
                           + "/10 (" + movie.getVoteCount() + " votes)");
        userRatingBar.setRating((float)movie.getVoteAverage() / 2);
        myRatingBar.setRating((float)movie.getMyRating());
        myReview.setText(movie.getMyReview());
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

    private void setYearText(Movie movie, TextView year) {
        if (movie.getReleaseDate() == null || movie.getReleaseDate().isEmpty()) {
            year.setVisibility(View.GONE);
        } else {
            year.setVisibility(View.VISIBLE);
            year.setText("(" + movie.getReleaseDate().substring(0, YEAR_LENGTH) + ")");
        }
    }

    private class DownloadPosterTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadPosterTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
