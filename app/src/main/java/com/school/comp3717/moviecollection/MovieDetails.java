package com.school.comp3717.moviecollection;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetails extends Fragment {

    private static final int YEAR_LENGTH = 4;

    Movie movie;
    TextView title;
    TextView year;
    TextView synopsis;
    TextView director;
    TextView userRating;
    RatingBar userRatingBar;
    RatingBar myRatingBar;
    ImageView poster;
    EditText myReview;
    Button addOrRemoveButton;

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
        synopsis = (TextView) rootView.findViewById(R.id.synopsisBody);
        director = (TextView) rootView.findViewById(R.id.movieDirector);
        userRating = (TextView) rootView.findViewById(R.id.userRatingText);
        userRatingBar = (RatingBar) rootView.findViewById(R.id.userRatingBar);
        myRatingBar = (RatingBar) rootView.findViewById(R.id.myRatingBar);
        myReview = (EditText) rootView.findViewById(R.id.myReview);
        addOrRemoveButton = (Button) rootView.findViewById(R.id.addMovieButton);

        movie = this.getArguments().getParcelable("movie");
        setDetails();

        new DownloadPosterTask((ImageView) rootView.findViewById(R.id.moviePoster))
                .execute("http://image.tmdb.org/t/p/w185" + movie.getPosterUrl());

        return rootView;
    }

    private void setDetails() {
        title.setText(movie.getTitle());

        year.setText("(" + movie.getReleaseDate().substring(0, YEAR_LENGTH) + ")");

        String directedBy = "Directed by";
        String[] directorArray = movie.getDirector().split("\t");
        int numDirectors = 0;
        for(String directorName : directorArray){
            directedBy += (numDirectors == 0) ? " " : ", ";
            directedBy += directorName;
            numDirectors++;
        }
        director.setText(directedBy);

        synopsis.setText(movie.getSynopsis());

        userRating.setText(movie.getVoteAverage() + "/10 (" + movie.getVoteCount() + " votes)");
        userRatingBar.setRating((float)movie.getVoteAverage() / 2);
        myRatingBar.setRating((float)movie.getMyRating());
        myReview.setText(movie.getMyReview());

        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
        boolean currentState;
        if (dbHelper.getMovieById(movie.getMovieId()) == null){
            addOrRemoveButton.setText(R.string.add_movie_text);
            addOrRemoveButton.setBackgroundColor(getResources().getColor(R.color.green_300));
            currentState = true;
        }else{
            addOrRemoveButton.setText(R.string.remove_movie_text);
            addOrRemoveButton.setBackgroundColor(getResources().getColor(R.color.red_800));
            currentState = false;
        }

        addOrRemoveButton.setOnClickListener(
                new AddButtonOnClickListener(getActivity(), addOrRemoveButton, currentState));
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

    private class AddButtonOnClickListener implements View.OnClickListener{
        Context context;
        Button addOrRemoveButton;
        boolean showingAdd;

        public AddButtonOnClickListener(Context context, Button addOrRemoveButton, boolean showAdd){
            super();
            this.context = context;
            this.addOrRemoveButton = addOrRemoveButton;
            this.showingAdd = showAdd;
        }

        @Override
        public void onClick(View view) {
            addOrRemoveButton.setEnabled(false);
            MovieDbHelper dbHelper = new MovieDbHelper(getActivity());

            if (showingAdd){
                dbHelper.addMovie(movie);
                addOrRemoveButton.setText(R.string.remove_movie_text);
                addOrRemoveButton.setBackgroundColor(getResources().getColor(R.color.red_800));
                Toast.makeText(context, movie.getTitle() + " added to collection.", Toast.LENGTH_LONG).show();
            }else {
                dbHelper.removeMovieByID(movie.getMovieId());
                addOrRemoveButton.setText(R.string.add_movie_text);
                addOrRemoveButton.setBackgroundColor(getResources().getColor(R.color.green_300));
                Toast.makeText(context, movie.getTitle() + " removed from collection.", Toast.LENGTH_LONG).show();
            }

            showingAdd = !showingAdd;
            addOrRemoveButton.setEnabled(true);

        }
    }

}
