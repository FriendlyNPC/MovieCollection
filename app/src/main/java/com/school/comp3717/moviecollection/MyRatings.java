package com.school.comp3717.moviecollection;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyRatings extends Fragment {

    private static final int YEAR_LENGTH = 4;

    private ProgressBar ratingProgressBar;
    private TextView    ratingResults;
    private ListView    ratingItems;

    public MyRatings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_ratings, container, false);

        ratingResults     = (TextView)    rootView.findViewById(R.id.myRatingsResultsStatus);
        ratingProgressBar = (ProgressBar) rootView.findViewById(R.id.myRatingsProgressBar);
        ratingItems       = (ListView)    rootView.findViewById(R.id.myRatingsItemList);

        ratingResults.setVisibility(View.GONE);
        ratingItems.setVisibility(View.GONE);

        new GetAllRatingsTask().execute();

        // Inflate the layout for this fragment
        return rootView;
    }

    private class GetAllRatingsTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
        protected ArrayList<Movie> doInBackground(Void... params) {
            try {
                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                return dbHelper.getRatedMovies();
            } catch (Exception e) {
                String msg = (e.getMessage() == null) ? "Movie DB search failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            }
        }

        protected void onPostExecute(ArrayList<Movie> result) {

            ratingProgressBar.setVisibility(View.GONE);
            if (result == null){
                setRatingError();
            }else {
                setRatingResults(result);
            }
        }
    }

    public void setRatingError(){
        ratingResults.setText(R.string.db_error);
        ratingResults.setVisibility(View.VISIBLE);
    }

    public void setRatingResults(ArrayList<Movie> results){
        if (getActivity() == null) { return; } // Stop the crash on rapid back-button presses
        if (results == null || results.isEmpty()) {
            ratingResults.setText(R.string.no_results);
            ratingResults.setVisibility(View.VISIBLE);
        } else {
            RatingSearchItemArrayAdapter adapter = new RatingSearchItemArrayAdapter(getActivity(), results);
            ratingItems.setAdapter(adapter);
            ratingItems.setFastScrollEnabled(true);
            ratingItems.setVisibility(View.VISIBLE);
        }
    }

    private class RatingSearchItemArrayAdapter extends ArrayAdapter<Movie> {
        private final Context          context;
        private final ArrayList<Movie> movies;

        public RatingSearchItemArrayAdapter(Context context, ArrayList<Movie> movies) {
            super(context, R.layout.my_ratings_item, movies);
            this.context = context;
            this.movies = movies;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.my_ratings_item, parent, false);

            final ImageButton  expandButton  = (ImageButton)  rowView.findViewById(R.id.expandCollapseButton);
            final TextView     reviewHeader  = (TextView)     rowView.findViewById(R.id.ratingItemReviewHeader);
            final TextView     reviewBody    = (TextView)     rowView.findViewById(R.id.ratingItemReviewBody);
            final LinearLayout reviewWrapper = (LinearLayout) rowView.findViewById(R.id.ratingItemReviewWrapper);
            final Movie        movie         = movies.get(position);

            TextView  title        = (TextView)  rowView.findViewById(R.id.ratingItemTitle);
            RatingBar ratingBar    = (RatingBar) rowView.findViewById(R.id.ratingItemValue);
            String    release      = movie.getReleaseDate();

            if (release == null || release.trim().isEmpty()) {
                title.setText(movie.getTitle());
            } else {
                title.setText(movie.getTitle() + " (" + release.substring(0, YEAR_LENGTH) + ")");
            }

            title.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setMovie(movie);
                }
            });

            if (movie.getMyRating() < 0) {
                ratingBar.setVisibility(View.GONE);
            } else {
                ratingBar.setRating((float)movie.getMyRating());
            }

            if (movie.getMyReview() != null) {
                reviewBody.setText(movie.getMyReview());

                expandButton.setOnClickListener(new ImageButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String show = "Show Review";
                        String hide = "Hide Review";

                        if (reviewWrapper.getVisibility() == View.GONE) {
                            reviewWrapper.setVisibility(View.VISIBLE);
                            expandButton.setImageDrawable(getContext().getResources()
                                                              .getDrawable(R.drawable.arrow_up));
                            reviewHeader.setText(hide);
                        } else {
                            reviewWrapper.setVisibility(View.GONE);
                            expandButton.setImageDrawable(getContext().getResources()
                                                              .getDrawable(R.drawable.arrow_down));
                            reviewHeader.setText(show);
                        }
                    }
                });
            } else {
                reviewBody.setVisibility(View.GONE);
                reviewHeader.setVisibility(View.GONE);
                reviewWrapper.setVisibility(View.GONE);
                expandButton.setVisibility(View.GONE);
            }

            return rowView;
        }
    }
}
