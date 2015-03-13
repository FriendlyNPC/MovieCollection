package com.school.comp3717.moviecollection;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.school.comp3717.moviecollection.tools.RecyclerAdapter;
import com.school.comp3717.moviecollection.tools.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private static final int LIST_SIZE = 10;

    private MovieDbHelper movieDbHelper;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        movieDbHelper = new MovieDbHelper(getActivity());

        ArrayList<Movie> recentlyAdded = getRecentlyAdded();
        ArrayList<Movie> justWatched   = getJustWatched();

        ArrayList<Movie> newReleases = new ArrayList<>();
        Collections.addAll(newReleases, new Movie(), new Movie(), new Movie(), new Movie(), new Movie());

        RecyclerView recyclerViewRA = (RecyclerView) rootView.findViewById(R.id.recyclerViewRA);
        RecyclerView recyclerViewJW = (RecyclerView) rootView.findViewById(R.id.recyclerViewJW);
        RecyclerView recyclerViewNR = (RecyclerView) rootView.findViewById(R.id.recyclerViewNR);
        TextView     headerRA       = (TextView)     rootView.findViewById(R.id.recentlyAddedHeader);
        TextView     headerJW       = (TextView)     rootView.findViewById(R.id.justWatchedHeader);
        TextView     headerNR       = (TextView)     rootView.findViewById(R.id.newReleasesHeader);

        setRecyclerView(recentlyAdded, recyclerViewRA, headerRA);
        setRecyclerView(justWatched, recyclerViewJW, headerJW);
        setRecyclerView(newReleases, recyclerViewNR, headerNR);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setRecyclerView(ArrayList<Movie> movies,
                                 RecyclerView recyclerView,
                                 TextView header) {
        if (movies.isEmpty()) {
            header.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            RecyclerAdapter myAdapter = new RecyclerAdapter(movies);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setAdapter(myAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            addRecyclerViewListener(movies, getActivity(), recyclerView);
        }
    }

    private void addRecyclerViewListener(final ArrayList<Movie> movies,
                                         Context context,
                                         RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.setMovie(movies.get(position));
                    }
                })
        );
    }

    private ArrayList<Movie> getRecentlyAdded() {
        ArrayList<Movie> recentlyAdded = movieDbHelper.getRecentlyAdded();
        int myListSize = recentlyAdded.size();
        if (myListSize > LIST_SIZE) {
            recentlyAdded.subList(LIST_SIZE, myListSize).clear();
        }
        return recentlyAdded;
    }

    private ArrayList<Movie> getJustWatched() {
        ArrayList<Movie> justWatched = movieDbHelper.getJustWatched();
        int myListSize = justWatched.size();
        if (myListSize > LIST_SIZE) {
            justWatched.subList(LIST_SIZE, myListSize).clear();
        }
        return justWatched;
    }

    /*
    private ArrayList<Movie> getNewReleases() {
        ArrayList<Movie> newReleases = new ArrayList<>();
    }*/
}
