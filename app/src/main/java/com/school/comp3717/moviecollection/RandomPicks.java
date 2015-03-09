package com.school.comp3717.moviecollection;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RandomPicks extends Fragment {

    private static final int SEEKBAR_STEP = 5;
    private static final int SEEKBAR_MAX  = 185;
    private static final int SEEKBAR_MIN  = 60;

    private Spinner          genreSpinner;
    private Spinner          filmRatingSpinner;
    private CheckBox         unwatchedCheckBox;
    private String           genre;
    private String           filmRating;
    private int              runtime = 60;
    private boolean          isUnwatched = false;
    private ArrayList<Movie> randomPicks = new ArrayList<>();

    public RandomPicks() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SeekBar  runtimeSeekBar;
        TextView runtimeValue;
        Button   submitButton;

        View rootView = inflater.inflate(R.layout.fragment_random_picks, container, false);

        genreSpinner = (Spinner) rootView.findViewById(R.id.genreSpinner);
        filmRatingSpinner = (Spinner) rootView.findViewById(R.id.filmRatingSpinner);
        runtimeSeekBar = (SeekBar) rootView.findViewById(R.id.runtimeSeekBar);
        runtimeValue = (TextView) rootView.findViewById(R.id.runtimeValue);
        unwatchedCheckBox = (CheckBox) rootView.findViewById(R.id.unwatchedCheckBox);
        submitButton = (Button) rootView.findViewById(R.id.prefSubmitButton);

        genreSpinner.setAdapter(loadSpinnerData("genre"));
        filmRatingSpinner.setAdapter(loadSpinnerData("filmRating"));
        setSeekBar(runtimeSeekBar, runtimeValue);
        setSubmitButton(submitButton);

        // Inflate the layout for this fragment
        return rootView;
    }

    private ArrayAdapter loadSpinnerData(String column) {
        MovieDbHelper db = new MovieDbHelper(getActivity());
        // Spinner Drop down elements
        List<String> choices = db.getAllChoices(column);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                                                              android.R.layout.simple_spinner_item,
                                                              choices);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        return dataAdapter;
    }

    public void setSeekBar(final SeekBar seekBar, final TextView value) {
        /* If you want values from 3 to 5 with a step of 0.1 (3, 3.1, 3.2, ..., 5)
        *  this means that you have 21 possible values in the seekBar.
        *  So the range of the seek bar will be [0 ; (5-3)/0.1 = 20].
        */
        seekBar.setMax((SEEKBAR_MAX - SEEKBAR_MIN) / SEEKBAR_STEP);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar runtimeSeekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar runtimeSeekBar) {}

            @Override
            public void onProgressChanged(SeekBar runtimeSeekBar, int progress, boolean fromUser) {
                int seekBarValue = SEEKBAR_MIN + (progress * SEEKBAR_STEP);
                if (seekBarValue == SEEKBAR_MAX) {
                    runtime = 9999;
                    value.setText("No limit");
                } else {
                    runtime = seekBarValue;
                    value.setText(Integer.valueOf(seekBarValue).toString() + " min");
                }
            }
        });
    }

    public void setSubmitButton(Button button) {
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                genre = (String)genreSpinner.getSelectedItem();
                filmRating = (String)filmRatingSpinner.getSelectedItem();
                isUnwatched = unwatchedCheckBox.isChecked();
                randomPicks = dbHelper.getRandomPicks(genre, filmRating, runtime, isUnwatched);
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.setRandomPicks(randomPicks);
            }
        });
    }
}
