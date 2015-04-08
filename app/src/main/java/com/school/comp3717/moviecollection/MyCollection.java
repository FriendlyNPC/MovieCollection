package com.school.comp3717.moviecollection;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.school.comp3717.moviecollection.collection.GenreFilterDialog;
import com.school.comp3717.moviecollection.collection.MovieFilter;
import com.school.comp3717.moviecollection.tools.RangeSeekBar;
import com.school.comp3717.moviecollection.tools.ViewGroupUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCollection extends Fragment{

    public static final int               DIALOG_FRAGMENT = 1;
    private static final int              SEEKBAR_STEP = 5;
    private static final int              YEAR_LENGTH  = 4;
    private static final int              SEEKBAR_MAX  = 185;
    private static final int              SEEKBAR_MIN  = 60;
    private static final int              NO_LIMIT     = 9999;
    private static final String           TAG          = "RandomPicks";
    private static final SimpleDateFormat DATE_FORMAT  = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat YEAR_FORMAT  = new SimpleDateFormat("yyyy");

    //main listview stuff
    private ProgressBar             collectionProgressBar;
    private TextView                collectionResults;
    private ListView                collectionItems;
    private SlidingUpPanelLayout    slidePanel;

    //sliding panel stuff
    private Spinner     sortBySpinner;
    private Spinner     filmRatingSpinner;
    private String      filmRating;
    private int         runtime            = NO_LIMIT;
    private String      minReleaseDate;
    private String      maxReleaseDate;
    private String[]    genres;
    private Switch      orderBy;
    private Button      filterGenres;
    private Button      filterOk;

    //filtering stuff;
    private MovieFilter filter;

    public MyCollection() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_collection, container, false);

        SeekBar runtimeSeekBar;
        TextView runtimeValue;
        SeekBar oldSeekBar;
        TextView yearRangeMin;
        TextView yearRangeMax;

        //filter
        filter = new MovieFilter();


        //main view stuff
        collectionResults       = (TextView)            rootView.findViewById(R.id.myCollectionResultsStatus);
        collectionProgressBar   = (ProgressBar)         rootView.findViewById(R.id.myCollectionProgressBar);
        collectionItems         = (ListView)            rootView.findViewById(R.id.myCollectionItemList);
        slidePanel              = (SlidingUpPanelLayout)rootView.findViewById(R.id.sliding_layout);

        //sliding view stuff
        sortBySpinner       = (Spinner) rootView.findViewById(R.id.filter_sort_by);
        filmRatingSpinner   = (Spinner) rootView.findViewById(R.id.filter_rating);
        runtime             = NO_LIMIT;

        runtimeSeekBar    = (SeekBar)  rootView.findViewById(R.id.filter_runtimeSeekBar);
        runtimeValue      = (TextView) rootView.findViewById(R.id.filter_runtimeValue);
        oldSeekBar        = (SeekBar)  rootView.findViewById(R.id.filter_yearRangeSeekBar);
        yearRangeMin      = (TextView) rootView.findViewById(R.id.filter_yearRangeMin);
        yearRangeMax      = (TextView) rootView.findViewById(R.id.filter_yearRangeMax);
        orderBy           = (Switch) rootView.findViewById(R.id.filter_sort_switch);
        filterGenres      = (Button) rootView.findViewById(R.id.filter_genre_button);
        filterOk          = (Button) rootView.findViewById(R.id.filter_ok_button);

        String[] myResSortByArray = getResources().getStringArray(R.array.filter_sort_options);
        List<String> sortByList = Arrays.asList(myResSortByArray);

        orderBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter.setIsAscending(isChecked);
            }
        });


        sortBySpinner.setAdapter(loadSpinnerData(sortByList));

        filmRatingSpinner.setAdapter(loadSpinnerData("filmRating"));


        setSeekBar(runtimeSeekBar, runtimeValue);
        setFilterButton(filterGenres);
        setOkButton(filterOk);
        setRangeSeekBar(oldSeekBar, yearRangeMin, yearRangeMax);


        collectionResults.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.GONE);
        collectionItems.setVisibility(View.GONE);

        slidePanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                //no change
            }

            @Override
            public void onPanelCollapsed(View view) {
                collectionItems.setEnabled(true);
                collectionItems.refreshDrawableState();

                filter.setSort(sortBySpinner.getSelectedItemPosition());
                filter.setMaxRating((String) filmRatingSpinner.getSelectedItem());
                doFilterQuery();
            }

            @Override
            public void onPanelExpanded(View view) {
                collectionItems.setEnabled(false);
            }

            @Override
            public void onPanelAnchored(View view) {
                //no change
            }

            @Override
            public void onPanelHidden(View view) {
                //no change
            }
        });

        collectionProgressBar.setVisibility(View.VISIBLE);
        new GetAllMoviesTask().execute();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.my_collection_header);
    }

    private void setFilterButton(Button button){
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createFilterDialog();
            }
        });
    }

    private void setOkButton(Button button){
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                slidePanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
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
                ((TextView) v).setTextColor(getResources().getColorStateList(R.color.green_800));
                return v;
            }
        };
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        return dataAdapter;
    }

    private ArrayAdapter loadSpinnerData(List<String> choices) {
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
                ((TextView) v).setTextColor(getResources().getColorStateList(R.color.green_800));
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
            public void onStopTrackingTouch(SeekBar runtimeSeekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar runtimeSeekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar runtimeSeekBar, int progress, boolean fromUser) {
                int seekBarValue = SEEKBAR_MIN + (progress * SEEKBAR_STEP);
                if (seekBarValue == SEEKBAR_MAX) {
                    runtime = NO_LIMIT;
                    value.setText("No limit");
                    filter.setMaxRuntime(filter.MAX_RUNTIME);
                } else {
                    runtime = seekBarValue;
                    filter.setMaxRuntime(seekBarValue);
                    value.setText(Integer.valueOf(seekBarValue).toString() + " min");

                }
            }
        });
    }

    public void doFilterQuery(){
        collectionResults.setVisibility(View.GONE);
        collectionItems.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.VISIBLE);

        getFilteredMoviesTask collectionMoviesTask = new getFilteredMoviesTask();
        collectionMoviesTask.execute(filter);
    }


    public void setCollectionResults(List<Movie> results){
        if(getActivity() == null){return;} //stop the crash on rapid back-button presses
        if (results == null || results.isEmpty()){
            collectionResults.setText(R.string.no_results);
            collectionResults.setVisibility(View.VISIBLE);
        }else{
            CollectionSearchItemArrayAdapter adapter = new CollectionSearchItemArrayAdapter(getActivity(),
                    results);
            collectionItems.setAdapter(adapter);

            collectionItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CollectionSearchItemArrayAdapter adapter = (CollectionSearchItemArrayAdapter) parent.getAdapter();
                    Movie selected = adapter.getMovie(position);
                    collectionItems.setVisibility(View.GONE);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setMovie(selected);
                }
            });
            collectionItems.setVisibility(View.VISIBLE);
        }
    }

    public void setCollectionError(){
        collectionResults.setText(R.string.db_error);
        collectionResults.setVisibility(View.VISIBLE);
    }

    private class QueryCollectionMoviesTask extends AsyncTask<String, Void, List<Movie>> {
        protected List<Movie> doInBackground(String... query) {
            try {
                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                return dbHelper.searchMovie(query[0]);
            } catch (Exception e) {
                String msg = (e.getMessage() == null) ? "MovieDB search failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            }
        }

        protected void onPostExecute(List<Movie> result) {

            collectionProgressBar.setVisibility(View.GONE);
            if (result == null){
                setCollectionError();
            }else {
                setCollectionResults(result);
            }
        }

    }

    private class GetAllMoviesTask extends AsyncTask<Void, Void, List<Movie>> {
        protected List<Movie> doInBackground(Void... params) {
            try {
                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                return dbHelper.getMovieCollectionSorted();
            } catch (Exception e) {
                String msg = (e.getMessage() == null) ? "Movie DB search failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            }
        }

        protected void onPostExecute(List<Movie> result) {

            collectionProgressBar.setVisibility(View.GONE);
            if (result == null){
                setCollectionError();
            }else {
                setCollectionResults(result);
            }
        }

    }

    private class CollectionSearchItemArrayAdapter extends ArrayAdapter<Movie>{
        private final Context context;
        private final List<Movie> movies;

        public CollectionSearchItemArrayAdapter(Context context, List<Movie> objects) {
            super(context, R.layout.search_result_collection_item, objects);
            this.context = context;
            this.movies = objects;

        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.search_result_collection_item,parent,false);
            TextView movieTitle = (TextView) rowView.findViewById(R.id.resultCollectionItemTitle);
            TextView movieYear = (TextView) rowView.findViewById(R.id.resultCollectionItemYear);

            movieTitle.setText(movies.get(position).getTitle());
            String release = movies.get(position).getReleaseDate();
            if (release == null || release.trim().isEmpty()){
                movieYear.setText("");
            } else{
                movieYear.setText("(" + release.substring(0, YEAR_LENGTH) + ")");
            }
            return rowView;
        }

        public Movie getMovie(int position){
            return movies.get(position);
        }

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

    private Date getMinDate() {
        MovieDbHelper db = new MovieDbHelper(getActivity());
        String dateStr = db.getMinReleaseDate();
        Date date = null;
        try {
            if (dateStr == null || dateStr.isEmpty()){
                dateStr = "1900-01-01";
            }
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
        filter.setMinDate(minReleaseDate);
        filter.setMaxDate(maxReleaseDate);
    }



    public void createFilterDialog(){
        MovieDbHelper db = new MovieDbHelper(getActivity());
        // Spinner Drop down elements
        List<String> choices = db.getAllChoices("Genre");
        Bundle args = new Bundle();

        String[] genreArray = choices.toArray(new String[choices.size()]);
        args.putStringArray("genres", genreArray);

        DialogFragment dialog = new GenreFilterDialog();

        dialog.setArguments(args);
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode) {
            case DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("Filter Query", "dialog got results " + data.getStringArrayListExtra("selected").toString());
                    filter.setGenres(data.getStringArrayListExtra("selected"));
                } else if (resultCode == Activity.RESULT_CANCELED){
                    // After Cancel code.
                }
                break;
        }
    }


    public class getFilteredMoviesTask extends AsyncTask<MovieFilter, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(MovieFilter... params) {
            try {
                MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
                return dbHelper.getMovieByFilter(params[0]);
            } catch (Exception e) {
                String msg = (e.getMessage() == null) ? "MovieDB search failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> results){
            collectionProgressBar.setVisibility(View.GONE);
            if (results == null){
                setCollectionError();
            }else {
                setCollectionResults(results);
            }
        }
    }

}
