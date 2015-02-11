package com.school.comp3717.moviecollection.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.school.comp3717.moviecollection.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResults extends Fragment {
    private ViewPager mViewPager;
    private SearchViewPagerAdapter mSearchViewPagerAdapter;
    private String query;

    public SearchResults() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = new Bundle();
        String query = this.getArguments().getString("QUERY");
        query = sanitize(query);
        Log.d("SearchResult", "Query: " + query);
        bundle.putString("QUERY", query);

        mSearchViewPagerAdapter = new SearchViewPagerAdapter(getChildFragmentManager(), bundle);
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        mViewPager = (ViewPager) rootView.findViewById(R.id.searchPager);
        mViewPager.setAdapter(mSearchViewPagerAdapter);

        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.searchTabs);
        tabsStrip.setViewPager(mViewPager);
        tabsStrip.setTextColor(getResources().getColor(R.color.grey_0));

        return rootView;
    }

    private String sanitize(String toQuery){
        //TODO: sanitize the search value
        return toQuery;
    }

    /**
     * perform a new query. Only call this if the fragment is already active and visible, otherwise
     * just push an argumetn to the fragment with the "QUERY" key.
     * @param query
     */
    public void doQuery(String query){
        query = sanitize(query);
        CollectionResultsList crl = (CollectionResultsList) getChildFragmentManager().findFragmentByTag(getFragmentTag(mViewPager.getId(), 0));
        OnlineResultsList orl = (OnlineResultsList) getChildFragmentManager().findFragmentByTag(getFragmentTag(mViewPager.getId(), 1));

        crl.doQuery(query);
        orl.doQuery(query);
    }

    private String getFragmentTag(int viewPagerId, int fragmentPosition){
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

    public class SearchViewPagerAdapter extends FragmentPagerAdapter{
        final private int PAGE_COUNT = 2;
        private String tabTitles[] = new String[] {getResources().getString(R.string.collection_tab_title),
                getResources().getString(R.string.online_tab_title)};
        private Bundle queryBundle;

        public SearchViewPagerAdapter(FragmentManager fm, Bundle fragmentBundle) {
            super(fm);
            queryBundle = fragmentBundle;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    CollectionResultsList crl = new CollectionResultsList();
                    crl.setArguments(queryBundle);
                    return crl;
                case 1:
                    OnlineResultsList orl = new OnlineResultsList();
                    orl.setArguments(queryBundle);
                    return orl;
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position){
            return tabTitles[position];
        }
    }
}
