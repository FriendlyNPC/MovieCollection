package com.school.comp3717.moviecollection;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.school.comp3717.moviecollection.search.SearchResults;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends ActionBarActivity {
    private DrawerLayout            drawerLayout;
    private ListView                listView;
    private ActionBarDrawerToggle   actionBarDrawerToggle;
    private Toolbar                 toolbar;
    private String[]                navigationDrawerItems;
    private Fragment[]              appFragments;
    private Random                  randomizer = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationDrawerItems = getResources().getStringArray(R.array.navigation_drawer_items);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_drawer);

        setSupportActionBar(toolbar);

        // set a custom shadow that overlays the main content when the drawer opens
        // drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.nav_item, navigationDrawerItems));
        listView.setOnItemClickListener(new DrawerItemClickListener());

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // secondary screens
        appFragments = new Fragment[6];
        appFragments[0] = new Home();
        appFragments[1] = new MyCollection();
        //appFragments[2] = new MyLists();
        appFragments[2] = new MyRatings();
        appFragments[3] = new RandomPicks();
        appFragments[4] = new MovieMetrics();
        appFragments[5] = new About();

        // set home screen
        selectItem(0);

        // Create default options which will be used for every
        // displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(500))
                .build();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);
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
        FragmentManager fragmentManager = getSupportFragmentManager();

        //if we switch to any nav drawer item, we crush the backstack
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentManager.beginTransaction().replace(R.id.content_frame, appFragments[position]).commit();


        // update selected item and title, then close the drawer
        listView.setItemChecked(position, true);
        setTitle(navigationDrawerItems[position]);
        drawerLayout.closeDrawer(listView);
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
        // Pass any configuration change to the drawer toggles
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
            Log.d("SearchAction", "Query: " + query);
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.popBackStackImmediate(getResources().getString(R.string.search_results_tag),FragmentManager.POP_BACK_STACK_INCLUSIVE);

            SearchResults resultsFragment = (SearchResults) fragmentManager.findFragmentByTag(getResources().getString(R.string.search_results_tag));

            if (resultsFragment == null) { //fragment is not active
                Log.d("SearchAction", "fragment not active");

                resultsFragment = new SearchResults();

                Bundle args = new Bundle();
                args.putString("QUERY", query);

                resultsFragment.setArguments(args);

                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, resultsFragment, getResources().getString(R.string.search_results_tag))
                        .addToBackStack(getResources().getString(R.string.search_results_tag))
                        .commit();

            } else if(!resultsFragment.isVisible()){ //performed a search, but left screen
                Log.d("SearchAction", "fragment not visible");

                resultsFragment.getArguments().putString("QUERY", query);

                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, resultsFragment,getResources().getString(R.string.search_results_tag))
                        .addToBackStack(getResources().getString(R.string.search_results_tag))
                        .commit();

            } else { //we're still on the search results screen
                //fragmentManager.popBackStackImmediate(getResources().getString(R.string.search_results_tag),0);
                Log.d("SearchAction", "fragment active & visible");
                resultsFragment.doQuery(query);
            }

            // update selected item and title, then close the drawer
            //listView.setItemChecked(position, true); //TODO: remove disable highlighted nav item
            setTitle(R.string.search_results);
            drawerLayout.closeDrawer(listView);
        }
    }

    //handle the back button being pressed
    @Override
    public void onBackPressed(){
        FragmentManager sfm = getSupportFragmentManager();
        if (sfm.getBackStackEntryCount() > 0) {
            sfm.popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    public void setMovie(Movie movie) {
        Log.d("SetMovie" , "Movie to set: " + movie.getTitle());
        FragmentManager fragmentManager = getSupportFragmentManager();

        MovieDetails movieDetailFragment = new MovieDetails();

        Bundle args = new Bundle();
        args.putParcelable("movie", movie);

        movieDetailFragment.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, movieDetailFragment, getResources().getString(R.string.movie_details_tag))
                .addToBackStack(getResources().getString(R.string.movie_details_tag))
                .commit();

        // update selected item and title, then close the drawer
        //listView.setItemChecked(position, true); //TODO: remove disable highlighted nav item
        setTitle(R.string.movie_details_toolbar);
        drawerLayout.closeDrawer(listView);
    }

    public void setRandomPicks(ArrayList<Movie> randomPicks) {
        Log.d("SetRandomPicks" , "Random picks set");

        Movie movie = null;

        if (!randomPicks.isEmpty()) {
            int choice = randomizer.nextInt(randomPicks.size());
            movie = randomPicks.get(choice);
            randomPicks.remove(choice);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        // TODO: Look into whether this is the correct way to implement this
        // Pop previous RandomPicksResult fragments
        fragmentManager.popBackStack();

        RandomPicksResult picksResultFragment = new RandomPicksResult();

        Bundle args = new Bundle();
        args.putParcelable("pick", movie);
        args.putParcelableArrayList("randomPicks", randomPicks);

        picksResultFragment.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, picksResultFragment, getResources().getString(R.string.random_picks_result_tag))
                .addToBackStack(getResources().getString(R.string.random_picks_result_tag))
                .commit();

        // Update selected item and title, then close the drawer
        setTitle(R.string.random_picks_result_toolbar);
        drawerLayout.closeDrawer(listView);
    }

    /*
    public void listDetailsClick(View v) {
        ListDetails listDetailsFragment = new ListDetails();
        FragmentManager fragManager = getSupportFragmentManager();
        fragManager.beginTransaction()
                .replace(R.id.myLists, listDetailsFragment)
                .addToBackStack(null)
                .commit();
    }*/
}