package com.school.comp3717.moviecollection;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.school.comp3717.moviecollection.tools.DownloadPosterTask;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class RandomPicksResult extends Fragment {

    private static final int YEAR_LENGTH = 4;

    private Random           randomizer  = new Random();
    private Movie            movie;
    private ArrayList<Movie> randomPicks;
    private TextView         title;
    private int              choice;
    private View             rootView;

    public RandomPicksResult() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // If rootView not null, use again so same random pick is shown when pressing back
        if (rootView != null) {
            // Remove the view from the parent
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        } else {
            ImageButton posterButton;
            Button nextPickButton;

            rootView = inflater.inflate(R.layout.fragment_random_picks_result, container, false);

            randomPicks = this.getArguments().getParcelableArrayList("randomPicks");

            posterButton = (ImageButton) rootView.findViewById(R.id.randomPicksPoster);
            title = (TextView) rootView.findViewById(R.id.randomPicksTitle);
            nextPickButton = (Button) rootView.findViewById(R.id.nextPickButton);

            if (randomPicks.isEmpty()) {
                posterButton.setImageResource(R.drawable.sad_face);
                title.setText(getResources().getString(R.string.results_criteria));
                nextPickButton.setVisibility(View.INVISIBLE);
            } else {
                choice = randomizer.nextInt(randomPicks.size());
                movie = randomPicks.get(choice);

                // If no more picks to show, remove next pick button
                if (randomPicks.size() <= 1) {
                    nextPickButton.setVisibility(View.INVISIBLE);
                }

                setDetails();

                if (movie.getPosterUrl() != null) {
                    new DownloadPosterTask(getActivity(), (ImageButton) rootView.findViewById(R.id.randomPicksPoster))
                            .execute("http://image.tmdb.org/t/p/w185" + movie.getPosterUrl());
                }

                setPosterButton(posterButton);
                setNextPickButton(nextPickButton);
            }
        }
        // Inflate the layout for this fragment
        return rootView;
    }

    private void setDetails() {
        setTitleText(movie, title);
    }

    private void setTitleText(Movie movie, TextView movieTitle) {
        String title = movie.getTitle();
        String year;
        if (movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty()) {
            year = " (" + movie.getReleaseDate().substring(0, YEAR_LENGTH) + ")";
            title += "\n" + year;
        }
        movieTitle.setText(title);
    }

    public void setNextPickButton(Button button) {
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomPicks.remove(choice);
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.setRandomPicks(randomPicks);
            }
        });
    }

    public void setPosterButton(ImageButton button) {
        button.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setMovie(movie);
            }
        });
    }
}