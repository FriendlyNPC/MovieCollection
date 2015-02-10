package com.school.comp3717.moviecollection;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.omertron.themoviedbapi.model.MovieDb;

public class MainActivity extends ActionBarActivity {
    private DrawerLayout            drawerLayoutt;
    private ListView                listView;
    private ActionBarDrawerToggle   actionBarDrawerToggle;
    private Toolbar                 toolbar;

    private String[]                navigationDrawerItems;
    private Fragment[]              appFragments;

    private MovieDbHelper           mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationDrawerItems = getResources().getStringArray(R.array.navigation_drawer_items);
        drawerLayoutt = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);

        setSupportActionBar(toolbar);

        // set a custom shadow that overlays the main content when the drawer opens
        // drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.nav_item, navigationDrawerItems));
        listView.setOnItemClickListener(new DrawerItemClickListener());

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayoutt, toolbar, R.string.app_name, R.string.app_name);
        drawerLayoutt.setDrawerListener(actionBarDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            appFragments = new Fragment[9];
            appFragments[0] = new Home();
            appFragments[1] = new MyCollection();
            appFragments[2] = new MyLists();
            appFragments[3] = new MyRatings();
            appFragments[4] = new RandomPicks();
            appFragments[5] = new MovieMetrics();
            appFragments[6] = new About();
            //secondary screens
            appFragments[7] = new SearchResults();
            appFragments[8] = new MovieDetails();

            //set homescreen
            selectItem(0);
        }

        // Create and initialize database and movie table
        mDbHelper = new MovieDbHelper(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        //set main view to selected fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, appFragments[position]).commit();

        // update selected item and title, then close the drawer
        listView.setItemChecked(position, true);
        setTitle(navigationDrawerItems[position]);
        drawerLayoutt.closeDrawer(listView);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                SearchView search = (SearchView)toolbar.getMenu().findItem(R.id.action_search).getActionView();
                search.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //stub
                return true;
            }
        });

        return true;
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            FragmentManager fragmentManager = getFragmentManager();
            SearchResults resultsFragment = (SearchResults) appFragments[7];

            if (fragmentManager.findFragmentByTag(getResources().getString(R.string.search_results_tag)) == null){
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, resultsFragment,getResources().getString(R.string.search_results_tag))
                        .addToBackStack(getResources().getString(R.string.search_results_tag))
                        .commit();
            }

            // update selected item and title, then close the drawer
            //listView.setItemChecked(position, true); //TODO: remove disable highlighted nav item
            setTitle(R.string.search_results);
            drawerLayoutt.closeDrawer(listView);

            //get the fragment showing before launching the query.
            fragmentManager.executePendingTransactions();
            getSupportFragmentManager().executePendingTransactions();

            resultsFragment.query(query);
        }
    }

    //handle the back button being pressed
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    public void setMovie(MovieDb movie)
    {
        FragmentManager fragmentManager = getFragmentManager();
        MovieDetails movieDetailFragment = (MovieDetails) appFragments[8];

        movieDetailFragment.setMovie(movie);

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, movieDetailFragment, getResources().getString(R.string.movie_details_tag))
                .addToBackStack(getResources().getString(R.string.movie_details_tag))
                .commit();

        // update selected item and title, then close the drawer
        //listView.setItemChecked(position, true); //TODO: remove disable highlighted nav item
        setTitle(R.string.movie_details_toolbar);
        drawerLayoutt.closeDrawer(listView);

        //get the fragment showing before launching the query.
        fragmentManager.executePendingTransactions();
        getSupportFragmentManager().executePendingTransactions();
    }
}