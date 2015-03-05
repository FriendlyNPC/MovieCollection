package com.school.comp3717.moviecollection;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //Dummy movie array for filler
        Movie movies[] = {new Movie(), new Movie(), new Movie(), new Movie(), new Movie()};
        Movie movies2[] = {new Movie(), new Movie(), new Movie(), new Movie(), new Movie()};
        Movie movies3[] = {new Movie(), new Movie(), new Movie(), new Movie(), new Movie()};
        RecyclerView recyclerView;
        RecyclerView recyclerView2;
        RecyclerView recyclerView3;
        RecyclerAdapter myAdapter = new RecyclerAdapter(movies);
        RecyclerAdapter myAdapter2 = new RecyclerAdapter(movies2);
        RecyclerAdapter myAdapter3 = new RecyclerAdapter(movies3);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        final LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView2 = (RecyclerView) rootView.findViewById(R.id.recyclerView2);
        recyclerView3 = (RecyclerView) rootView.findViewById(R.id.recyclerView3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView3.setLayoutManager(layoutManager3);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setAdapter(myAdapter);
        recyclerView2.setAdapter(myAdapter2);
        recyclerView3.setAdapter(myAdapter3);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setItemAnimator(new DefaultItemAnimator());

        // Inflate the layout for this fragment
        return rootView;
    }


}
