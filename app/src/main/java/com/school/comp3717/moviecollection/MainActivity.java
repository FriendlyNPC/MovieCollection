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

public class MainActivity extends ActionBarActivity {
    private DrawerLayout drawerLayoutt;
    private ListView listView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final String apiKey = "48a5ee87fadc129033207cea80b86b81";
    private Toolbar toolbar;

    private String[] navigationDrawerItems;

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
        //drawerLayoutt.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.nav_item, navigationDrawerItems));
        listView.setOnItemClickListener(new DrawerItemClickListener());

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayoutt, toolbar, R.string.app_name, R.string.app_name);
        drawerLayoutt.setDrawerListener(actionBarDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch(position){
            case(0):
                fragment = new Home();
                break;
            case(1):
                fragment = new MyCollection();
                break;
            case(2):
                fragment = new MyLists();
                break;
            case(3):
                fragment = new MyRatings();
                break;
            case(4):
                fragment = new RandomPicks();
                break;
            case(5):
                fragment = new MovieMetrics();
                break;
            case(6):
                fragment = new About();
                break;
            default:
                fragment = new Home();
        }

        Bundle args = new Bundle();
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

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
            SearchResults searchResults = new SearchResults();

            Bundle args = new Bundle();
            String query = intent.getStringExtra(SearchManager.QUERY);
            args.putString("query",query);
            searchResults.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, searchResults, "searchResults")
                    .addToBackStack("searchResults")
                    .commit();

            // update selected item and title, then close the drawer
            //listView.setItemChecked(position, true); //TODO: remove disable highlighted nav item
            setTitle(R.string.searchResults);
            drawerLayoutt.closeDrawer(listView);
        }
    }

    //handle the back button being pressed
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            //TODO:IF AT HOME SCREEN, CALL super.onBackPressed();
            fm.popBackStack();
        }else {
            super.onBackPressed();
        }
    }
}