package com.school.comp3717.moviecollection;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieMetrics extends Fragment {

    //to hold db results
    private ArrayList[] collection_genre;
    private ArrayList[] collection_film_ratings;
    private ArrayList[] viewing_genre;
    private ArrayList[] viewing_film_rating;
    private ArrayList[] star_ratings;

    //to display single results
    private TextView movie_count_view;
    private TextView watch_count_view;
    private TextView minutes_count_view;
    private TextView ratings_count_view;
    private TextView review_count_view;

    //Pie charts of metrics
    private PieChart collection_genres_chart;
    private PieChart collection_film_rating_chart;
    private PieChart viewing_genres_chart;
    private PieChart viewing_film_chart;
    private HorizontalBarChart star_rating_chart;

    public MovieMetrics() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_movie_metrics, container, false);

        collection_genres_chart         = (PieChart) rootView.findViewById(R.id.genre_collection_chart);
        collection_film_rating_chart    = (PieChart) rootView.findViewById(R.id.rating_collection_chart);
        viewing_genres_chart            = (PieChart) rootView.findViewById(R.id.genre_viewing_chart);
        viewing_film_chart              = (PieChart) rootView.findViewById(R.id.rating_viewing_chart);
        star_rating_chart               = (HorizontalBarChart) rootView.findViewById(R.id.movie_rating_chart);

        movie_count_view                = (TextView) rootView.findViewById(R.id.num_movies);
        watch_count_view                = (TextView) rootView.findViewById(R.id.num_movies_watched);
        minutes_count_view              = (TextView) rootView.findViewById(R.id.num_movies_watched_hours);
        ratings_count_view              = (TextView) rootView.findViewById(R.id.movies_rated);
        review_count_view               = (TextView) rootView.findViewById(R.id.movies_reviewed);

        setupCharts();
        getDatabaseMetrics();
        populateCharts();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.movie_metrics_header);
    }

    private void setupCharts(){
        collection_genres_chart.setVisibility(View.GONE);
        collection_genres_chart.setTouchEnabled(false);
        collection_genres_chart.setTouchEnabled(false);
        collection_genres_chart.setHoleColorTransparent(true);
        collection_genres_chart.setCenterText(getActivity().getResources()
                .getString(R.string.collection_genres));
        collection_genres_chart.setUsePercentValues(false);
        collection_genres_chart.setDrawSliceText(false);
        collection_genres_chart.animateXY(1500, 1500);
        collection_genres_chart.setDescription("");

        collection_film_rating_chart.setVisibility(View.GONE);
        collection_film_rating_chart.setTouchEnabled(false);
        collection_film_rating_chart.setHoleColorTransparent(true);
        collection_film_rating_chart.setCenterText(getActivity().getResources()
                .getString(R.string.collection_film_rating));
        collection_film_rating_chart.animateXY(1500, 1500);
        collection_film_rating_chart.setUsePercentValues(false);
        collection_film_rating_chart.setDrawSliceText(false);
        collection_film_rating_chart.setDescription("");

        viewing_genres_chart.setVisibility(View.GONE);
        viewing_genres_chart.setTouchEnabled(false);
        viewing_genres_chart.setHoleColorTransparent(true);
        viewing_genres_chart.setCenterText(getActivity().getResources()
                .getString(R.string.collection_genres));
        viewing_genres_chart.animateXY(1500, 1500);
        viewing_genres_chart.setUsePercentValues(false);
        viewing_genres_chart.setDrawSliceText(false);
        viewing_genres_chart.setDescription("");

        viewing_film_chart.setVisibility(View.GONE);
        viewing_film_chart.setTouchEnabled(false);
        viewing_film_chart.setHoleColorTransparent(true);
        viewing_film_chart.setCenterText(getActivity().getResources()
                .getString(R.string.collection_film_rating));
        viewing_film_chart.animateXY(1500, 1500);
        viewing_film_chart.setUsePercentValues(false);
        viewing_film_chart.setDrawSliceText(false);
        viewing_film_chart.setDescription("");

        star_rating_chart.setVisibility(View.GONE);
        star_rating_chart.setTouchEnabled(false);
        star_rating_chart.setDescription("");
        star_rating_chart.animateXY(1500, 1500);
    }

    private void getDatabaseMetrics(){
        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());

        movie_count_view.setText(dbHelper.getMovieCollectionCount());
        watch_count_view.setText(dbHelper.getMovieWatchCount());
        minutes_count_view.setText(dbHelper.getNumberOfMinutesWatched());
        ratings_count_view.setText(dbHelper.getMoviesRatedCount());
        review_count_view.setText(dbHelper.getReviewsCount());

        Log.i("Movie Metric", "Getting Data from database");

        collection_film_ratings     = dbHelper.getMovieCollectionFilmRatingCount();
        star_ratings                = dbHelper.getMovieCollectionMyRatingsCount();
        viewing_film_rating         = dbHelper.getMovieViewingFilmRatingCount();
        collection_genre            = dbHelper.getMovieCollectionGenreCount();
        viewing_genre               = dbHelper.getMovieViewingGenreCount();
        Log.i("Movie Metric" , ((ArrayList<String>)collection_genre[0]).toString() );
        Log.i("Movie Metric",  ((ArrayList<Long>) collection_genre[1]).toString() );

    }

    private void populateCharts(){
        //hide all charts to start, if we populate, then we can show them.

        ArrayList<Integer> colours = new ArrayList<Integer>();


        for(int c : ColorTemplate.COLORFUL_COLORS){
            colours.add(c);
        }

        for(int c : ColorTemplate.PASTEL_COLORS){
            colours.add(c);
        }

        for(int c : ColorTemplate.VORDIPLOM_COLORS){
            colours.add(c);
        }

        for(int c : ColorTemplate.LIBERTY_COLORS){
            colours.add(c);
        }

        colours.add(ColorTemplate.getHoloBlue());

        if(collection_film_ratings[0].size() > 0){
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            Float total = 0f;
            for(int i = 0; i < collection_film_ratings[1].size(); i++) {
                total += ((ArrayList<Long>)collection_film_ratings[1]).get(i);
            }
            for(int i = 0; i < collection_film_ratings[1].size(); i++) {
                yVals1.add(new Entry(((ArrayList<Long>) collection_film_ratings[1]).get(i) / total * 100, i));
            }

            PieDataSet dataset = new PieDataSet( yVals1, "Film Rating");
            dataset.setValueFormatter(new PercentFormatter());
            dataset.setSliceSpace(3f);
            dataset.setValueTextSize(11f);
            dataset.setValueTextSize(Color.WHITE);
            dataset.setColors(colours);
            PieData data = new PieData((ArrayList<String>)collection_film_ratings[0], dataset);
            collection_film_rating_chart.setData(data);

            Legend l = collection_film_rating_chart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);

            collection_film_rating_chart.setVisibility(View.VISIBLE);
        }

        if(collection_genre[0].size() > 0){
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            Float total = 0f;
            for(int i = 0; i < collection_genre[1].size(); i++) {
                total += ((ArrayList<Long>)collection_genre[1]).get(i);
            }
            for(int i = 0; i < collection_genre[1].size(); i++) {
                yVals1.add(new Entry(((ArrayList<Long>) collection_genre[1]).get(i) / total * 100, i));
            }

            PieDataSet dataset = new PieDataSet( yVals1, "Genres");
            dataset.setValueFormatter(new PercentFormatter());
            dataset.setSliceSpace(3f);
            dataset.setValueTextSize(11f);
            dataset.setValueTextSize(Color.WHITE);
            dataset.setColors(colours);
            PieData data = new PieData((ArrayList<String>)collection_genre[0], dataset);
            collection_genres_chart.setData(data);

            Legend l = collection_genres_chart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);

            collection_genres_chart.setVisibility(View.VISIBLE);
        }

        if(viewing_genre[0].size() > 0){
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            Float total = 0f;
            for(int i = 0; i < viewing_genre[1].size(); i++) {
                total += ((ArrayList<Long>)viewing_genre[1]).get(i);
            }
            for(int i = 0; i < viewing_genre[1].size(); i++) {
                yVals1.add(new Entry(((ArrayList<Long>) viewing_genre[1]).get(i) / total * 100, i));
            }

            PieDataSet dataset = new PieDataSet( yVals1, "Genres");
            dataset.setValueFormatter(new PercentFormatter());
            dataset.setSliceSpace(3f);
            dataset.setValueTextSize(11f);
            dataset.setValueTextSize(Color.WHITE);
            dataset.setColors(colours);
            PieData data = new PieData((ArrayList<String>)viewing_genre[0], dataset);

            viewing_genres_chart.setData(data);

            Legend l = viewing_genres_chart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);

            viewing_genres_chart.setVisibility(View.VISIBLE);
        }

        if(viewing_film_rating[0].size() > 0){
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            Float total = 0f;
            for(int i = 0; i < viewing_film_rating[1].size(); i++) {
                total += ((ArrayList<Long>)viewing_film_rating[1]).get(i);
            }
            for(int i = 0; i < viewing_film_rating[1].size(); i++) {
                yVals1.add(new Entry(((ArrayList<Long>) viewing_film_rating[1]).get(i) / total * 100, i));
            }

            PieDataSet dataset = new PieDataSet( yVals1, "Film Rating");
            dataset.setValueFormatter(new PercentFormatter());
            dataset.setSliceSpace(3f);
            dataset.setValueTextSize(11f);
            dataset.setValueTextSize(Color.WHITE);
            dataset.setColors(colours);
            PieData data = new PieData((ArrayList<String>)viewing_film_rating[0], dataset);
            viewing_film_chart.setData(data);

            Legend l = viewing_film_chart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);


            viewing_film_chart.setVisibility(View.VISIBLE);
        }

        if(star_ratings[0].size() > 0){
            ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
            for(int i = 0; i < star_ratings[1].size(); i++) {
                yVals1.add(new BarEntry( ((ArrayList<Long>)star_ratings[1]).get(i) ,i ));
            }

            BarDataSet dataset = new BarDataSet( yVals1, "Movies Rated");
            ArrayList<BarDataSet> datasets = new ArrayList<BarDataSet>();
            datasets.add(dataset);
            BarData data = new BarData((ArrayList<String>)star_ratings[0], datasets);
            star_rating_chart.setData(data);
            star_rating_chart.setVisibility(View.VISIBLE);
        }


    }

}
