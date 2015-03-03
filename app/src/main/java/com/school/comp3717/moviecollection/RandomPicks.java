package com.school.comp3717.moviecollection;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RandomPicks extends Fragment {

    private Spinner genreSpinner;
    private Spinner filmRatingSpinner;

    public RandomPicks() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_random_picks, container, false);
        genreSpinner = (Spinner) rootView.findViewById(R.id.genreSpinner);
        filmRatingSpinner = (Spinner) rootView.findViewById(R.id.filmRatingSpinner);
        genreSpinner.setAdapter(loadSpinnerData("genre"));
        filmRatingSpinner.setAdapter(loadSpinnerData("filmRating"));

        // Inflate the layout for this fragment
        return rootView;
    }

    private ArrayAdapter loadSpinnerData(String column) {
        MovieDbHelper db = new MovieDbHelper(getActivity());
        // Spinner Drop down elements
        List<String> choices = db.getAllChoices(column);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                                                                    android.R.layout.simple_spinner_item,
                                                                    choices);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        return dataAdapter;
    }
}
