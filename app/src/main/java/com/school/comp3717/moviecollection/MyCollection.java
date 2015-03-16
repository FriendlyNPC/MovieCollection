package com.school.comp3717.moviecollection;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCollection extends Fragment {

    private static final int YEAR_LENGTH = 4;
    private ProgressBar collectionProgressBar;
    private TextView collectionResults;
    private ListView collectionItems;

    public MyCollection() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_collection, container, false);
        collectionResults = (TextView) rootView.findViewById(R.id.myCollectionResultsStatus);
        collectionProgressBar = (ProgressBar) rootView.findViewById(R.id.myCollectionProgressBar);
        collectionItems = (ListView) rootView.findViewById(R.id.myCollectionItemList);
        collectionResults.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.GONE);
        collectionItems.setVisibility(View.GONE);

        new GetAllMoviesTask().execute();

        return rootView;
    }

    public void doQuery(String query){
        //TODO: do query stuff here

        collectionResults.setVisibility(View.GONE);
        collectionItems.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.VISIBLE);

        QueryCollectionMoviesTask collectionMoviesTask = new QueryCollectionMoviesTask();
        collectionMoviesTask.execute(query);
    }


    public void setCollectionResults(List<Movie> results){
        if(getActivity() == null){return;} //stop the crash on rapid back-button presses
        if (results == null || results.isEmpty()){
            collectionResults.setText(R.string.no_results);
            collectionResults.setVisibility(View.VISIBLE);
        }else{
            CollectionSearchItemArrayAdapter adapter = new CollectionSearchItemArrayAdapter(getActivity(),
                    results,SortType.TITLE);
            collectionItems.setAdapter(adapter);
            collectionItems.setFastScrollEnabled(true);
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

    private class CollectionSearchItemArrayAdapter extends ArrayAdapter<Movie> implements SectionIndexer{
        private final Context context;
        private final List<Movie> movies;
        private String[] sections;
        private HashMap<Integer,Integer> positionToSection;
        private HashMap<Integer,Integer> sectionToPosition;
        private SortType sortingMethod;

        public CollectionSearchItemArrayAdapter(Context context, List<Movie> objects, SortType sort) {
            super(context, R.layout.search_result_collection_item, objects);
            this.context = context;
            this.movies = objects;
            this.sortingMethod = sort;
            positionToSection = new HashMap<Integer,Integer>();
            sectionToPosition = new HashMap<Integer,Integer>();

            switch (sortingMethod){
                case TITLE:
                    sections = getTitleSections();
                    break;
                case RATING:
                    sections = getRatingSections();
                    break;
                case RUNTIME:
                    sections = getRuntimeSections();
                    break;
                case YEAR:
                    sections = getYearSections();
                    break;
                default:
                    //shouldn't get here, all enums accounted for
                    break;
            }
        }

        String[] getTitleSections(){
            ArrayList<String> section = new ArrayList<String>();
            String prevChar = "";
            String firstChar;
            int sectionIndex = 0;
            for(int i = 0; i < movies.size(); i++){
                firstChar = movies.get(i).getTitle().substring(0,1).toUpperCase();

                if (!firstChar.equals(prevChar)){
                    section.add(firstChar);
                    prevChar = firstChar;
                    sectionToPosition.put(sectionIndex, i);
                    positionToSection.put(i, sectionIndex);
                    sectionIndex++;
                }else{
                    positionToSection.put(i, sectionIndex);
                }
            }
            return section.toArray(new String[section.size()]);
        }

        String[] getRuntimeSections(){
            ArrayList<String> section = new ArrayList<String>();
            int prevRuntime = -1;
            int currentRuntime;
            int sectionIndex = 0;
            for(int i = 0; i < movies.size(); i++){
                currentRuntime = movies.get(i).getRuntime();
                //drop single minutes using integer division
                currentRuntime /=  10;
                currentRuntime *= 10;
                if (currentRuntime != prevRuntime){
                    section.add(Integer.toString(currentRuntime));
                    sectionToPosition.put(sectionIndex, i);
                    positionToSection.put(i, sectionIndex);
                    sectionIndex++;

                }else{
                    positionToSection.put(i, sectionIndex);
                }
            }
            return section.toArray(new String[section.size()]);
        }

        String[] getYearSections(){
            ArrayList<String> section = new ArrayList<String>();
            String prevYear = "";
            String currentYear;
            int sectionIndex = 0;
            for(int i = 0; i < movies.size(); i++){
                if(!movies.get(i).getReleaseDate().isEmpty()) {
                    currentYear = movies.get(i).getReleaseDate().substring(0, 4);
                }else{
                    currentYear = "";
                }

                if (!currentYear.equals(prevYear)){
                    section.add(prevYear);
                    prevYear = currentYear;
                    positionToSection.put(sectionIndex, i);
                    sectionToPosition.put(i, sectionIndex);
                    sectionIndex++;
                } else {
                    sectionToPosition.put(i, sectionIndex);
                }
            }

            return section.toArray(new String[section.size()]);
        }

        String[] getRatingSections(){
            ArrayList<String> section = new ArrayList<String>();
            String prevRating = "";
            String rating;
            int sectionIndex = 0;
            for(int i = 0; i < movies.size(); i++) {
                rating = movies.get(i).getFilmRating();
                if (!rating.equals(prevRating)) {
                    section.add(rating);
                    prevRating =  rating;
                    positionToSection.put(sectionIndex, i);
                    sectionToPosition.put(i, sectionIndex);
                    sectionIndex++;
                }else{
                    sectionToPosition.put(i, sectionIndex);
                }

            }
            return section.toArray(new String[section.size()]);
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

        @Override
        public Object[] getSections() {
            return sections;
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            return sectionToPosition.get(sectionIndex);
        }

        @Override
        public int getSectionForPosition(int position) {
            return positionToSection.get(position);
        }
    }

    public enum SortType{
        TITLE,
        YEAR,
        RATING,
        RUNTIME,
    }
}
