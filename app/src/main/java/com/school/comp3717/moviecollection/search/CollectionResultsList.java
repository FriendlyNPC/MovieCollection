package com.school.comp3717.moviecollection.search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.school.comp3717.moviecollection.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionResultsList extends Fragment {
    private ProgressBar collectionProgressBar;
    private TextView collectionResults;
    private ListView collectionItems;
    private String query;
    public CollectionResultsList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_collection_results_list, container, false);

        collectionResults = (TextView) rootView.findViewById(R.id.collectionResultsStatus);
        collectionProgressBar = (ProgressBar) rootView.findViewById(R.id.collectionProgressBar);
        collectionItems = (ListView) rootView.findViewById(R.id.collectionItemList);
        collectionResults.setVisibility(View.GONE);
        collectionProgressBar.setVisibility(View.GONE);
        collectionItems.setVisibility(View.GONE);

        String query = this.getArguments().getString("QUERY");
        Log.d("OnlineSearch", "Query: " + query);
        doQuery(query);

        return rootView;
    }

    public void doQuery(String movie){
        //TODO: do query stuff here
        collectionResults.setVisibility(View.VISIBLE);
        //collectionProgressBar.setVisibility(View.VISIBLE);
    }

    public void setCollectionResults(){
        //TODO:Display database results
        //if we have results, we show list items
        //collectionItems.setVisibility(View.VISIBLE);

        //otherwise we show the results text and say 0
        collectionResults.setVisibility(View.VISIBLE);
    }

    public void setCollectionError(){
        //display error message
        //collectionResults.setText("Error");
        collectionResults.setVisibility(View.VISIBLE);
    }

    //TODO: set ArrayAdapter to our DB movie
    /*
    private class OnlineSearchItemArrayAdapter extends ArrayAdapter<MovieDb> {
        private final Context context;
        private final List<MovieDb> movies;

        public OnlineSearchItemArrayAdapter(Context context, List<MovieDb> objects) {
            super(context, R.layout.search_result_online_item, objects);
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
            movieYear.setText(movies.get(position).getReleaseDate());

            return rowView;
        }

        public MovieDb getMovie(int position){
            return movies.get(position);
        }
    }
    */

    //TODO: Fix this up for our DB
    private class QueryOnlineMoviesTask extends AsyncTask<String, Void, List<String>> {
        protected List<String> doInBackground(String... query) {
            try {
                //TODO: do DB search here
                return new ArrayList<String>();
            } catch (Exception e) {
                String msg = (e.getMessage() == null) ? "MovieDB search failed!" : e.getMessage();
                Log.e("Search", msg);
                return null;
            }
        }

        protected void onPostExecute(List<String> result) {
            collectionProgressBar.setVisibility(View.GONE);
            if (result == null){
                setCollectionError();
            }else {
                setCollectionResults();
            }
        }

    }
}
