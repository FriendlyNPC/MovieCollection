package com.school.comp3717.moviecollection;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.school.comp3717.moviecollection.tools.RangeSeekBar;
import com.school.comp3717.moviecollection.tools.ViewGroupUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RandomPicks extends Fragment {

    private static final int              SEEKBAR_STEP = 5;
    private static final int              SEEKBAR_MAX  = 185;
    private static final int              SEEKBAR_MIN  = 60;
    private static final int              NO_LIMIT     = 9999;
    private static final String           TAG          = "RandomPicks";
    private static final SimpleDateFormat DATE_FORMAT  = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat YEAR_FORMAT  = new SimpleDateFormat("yyyy");

    private Spinner          genreSpinner;
    private Spinner          filmRatingSpinner;
    private CheckBox         unwatchedCheckBox;
    private String           genre;
    private String           filmRating;
    private int              runtime            = NO_LIMIT;
    private boolean          isUnwatched        = false;
    private ArrayList<Movie> randomPicks        = new ArrayList<>();
    private String           minReleaseDate;
    private String           maxReleaseDate;

    public RandomPicks() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SeekBar runtimeSeekBar;
        TextView runtimeValue;
        Button submitButton;
        SeekBar oldSeekBar;
        TextView yearRangeMin;
        TextView yearRangeMax;

        View rootView = inflater.inflate(R.layout.fragment_random_picks, container, false);

        genreSpinner      = (Spinner)  rootView.findViewById(R.id.genreSpinner);
        filmRatingSpinner = (Spinner)  rootView.findViewById(R.id.filmRatingSpinner);
        runtimeSeekBar    = (SeekBar)  rootView.findViewById(R.id.runtimeSeekBar);
        runtimeValue      = (TextView) rootView.findViewById(R.id.runtimeValue);
        unwatchedCheckBox = (CheckBox) rootView.findViewById(R.id.unwatchedCheckBox);
        submitButton      = (Button)   rootView.findViewById(R.id.prefSubmitButton);
        oldSeekBar        = (SeekBar)  rootView.findViewById(R.id.yearRangeSeekBar);
        yearRangeMin      = (TextView) rootView.findViewById(R.id.yearRangeMin);
        yearRangeMax      = (TextView) rootView.findViewById(R.id.yearRangeMax);

        genreSpinner.setAdapter(loadSpinnerData("genre"));
        filmRatingSpinner.setAdapter(loadSpinnerData("filmRating"));
        setSeekBar(runtimeSeekBar, runtimeValue);
        setSubmitButton(submitButton);
        setRangeSeekBar(oldSeekBar, yearRangeMin, yearRangeMax);

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
                                                                    choices) {
            public View getView(int position,
                                View convertView,
                                ViewGroup parent) {
                View v = super.getView(position,
                                       convertView,
                                       parent);
                ((TextView) v).setTextColor(getResources().getColorStateList(R.color.grey_800));
                return v;
            }
        };
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
        // Start at max runtime
        seekBar.setProgress(SEEKBAR_MAX);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar runtimeSeekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar runtimeSeekBar) {}

            @Override
            public void onProgressChanged(SeekBar runtimeSeekBar, int progress, boolean fromUser) {
                int seekBarValue = SEEKBAR_MIN + (progress * SEEKBAR_STEP);
                if (seekBarValue == SEEKBAR_MAX) {
                    runtime = NO_LIMIT;
                    value.setText("No limit");
                } else {
                    runtime = seekBarValue;
                    value.setText(Integer.valueOf(seekBarValue).toString() + " min");
                }
            }
        });
    }

    private Date getMinDate() {
        MovieDbHelper db = new MovieDbHelper(getActivity());
        String dateStr = db.getMinReleaseDate();
        Date date = null;
        try {
            date = DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, "DB min date parse exception", e);
        }
        return date;
    }

    private void setDateRange(TextView yearRangeMin,
                              TextView yearRangeMax,
                              Date minDate,
                              Date maxDate) {
        minReleaseDate = YEAR_FORMAT.format(minDate);
        maxReleaseDate = YEAR_FORMAT.format(maxDate);
        yearRangeMin.setText(minReleaseDate);
        yearRangeMax.setText(maxReleaseDate);
        minReleaseDate += "-01-01";
        maxReleaseDate += "-12-31";
    }

    public void setRangeSeekBar(SeekBar oldSeekBar,
                                final TextView yearRangeMin,
                                final TextView yearRangeMax) {
        Date minDate = new Date();
        if (getMinDate() != null) {
            minDate = getMinDate();
        } else {
            try {
                minDate = DATE_FORMAT.parse("1900-01-01");
            } catch (ParseException e) {
                Log.e(TAG, "Default min date parse exception", e);
            }
        }
        Date maxDate = new Date();
        setDateRange(yearRangeMin,
                     yearRangeMax,
                     minDate,
                     maxDate);
        RangeSeekBar<Long> seekBar = new RangeSeekBar<>(minDate.getTime(),
                                                        maxDate.getTime(),
                                                        getActivity());

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
                                                    Long minValue,
                                                    Long maxValue) {
                setDateRange(yearRangeMin,
                             yearRangeMax,
                             new Date(minValue),
                             new Date(maxValue));
                Log.d(TAG, "Date range selected: MIN = " + minReleaseDate + ", MAX = " + maxReleaseDate);
            }
        });

        // Replace a view with the RangeSeekBar that spans two columns
        ViewGroupUtils.replaceView(oldSeekBar, seekBar);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.span = 2;
        seekBar.setLayoutParams(layoutParams);
    }

    public void setSubmitButton(Button button) {
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                genre = (String)genreSpinner.getSelectedItem();
                filmRating = (String)filmRatingSpinner.getSelectedItem();
                isUnwatched = unwatchedCheckBox.isChecked();
                randomPicks = dbHelper.getRandomPicks(genre,
                                                      filmRating,
                                                      runtime,
                                                      isUnwatched,
                                                      minReleaseDate,
                                                      maxReleaseDate);
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.setRandomPicks(randomPicks);
            }
        });
    }
}
