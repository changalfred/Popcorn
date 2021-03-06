package com.example.android.popcorn;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.example.android.popcorn.activity.SearchResultsActivity;
import com.example.android.popcorn.fragment.CurrentFragment;
import com.example.android.popcorn.fragment.FavouriteFragment;
import com.example.android.popcorn.fragment.PopularFragment;
import com.example.android.popcorn.fragment.TopFragment;
import com.example.android.popcorn.ui.MovieCollectionPagerAdapter;
import com.example.android.popcorn.ui.TabTitles;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private final int PAGES_TO_RETAIN = 1;

    private final int SORT_DEFAULT = 0;
    private final int SORT_TOP_RATED = 1;
    private final int SORT_NAME_ALPHABETICAL = 2;
    private final int SORT_LONGEST_RUNTIME = 3;
    private final int SORT_NEWEST_RELEASE = 4;
    private final int SORT_HIGHEST_REVENUE = 5;
    private final int SORT_HIGHEST_PROFIT = 6;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.sort_category)
    TextView mSortCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupViewPager();
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void startSearch(String query) {
        Intent searchIntent = new Intent(this, SearchResultsActivity.class);
        searchIntent.putExtra(Utilities.SEARCH_KEY, query);
        searchIntent.putExtra(Utilities.PARENT_ACTIVITY, Utilities.MAIN_ACTIVITY_PARENT);
        startActivity(searchIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Source: https://developer.android.com/training/search/setup.html.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_icon).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setupViewPager() {
        MovieCollectionPagerAdapter pagerAdapter = new MovieCollectionPagerAdapter(getSupportFragmentManager());
        addFragments(pagerAdapter);
        addFragmentTitles(pagerAdapter);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(PAGES_TO_RETAIN);
    }

    private void addFragments(MovieCollectionPagerAdapter pagerAdapter) {
        pagerAdapter.addFragment(new PopularFragment());
        pagerAdapter.addFragment(new TopFragment());
        pagerAdapter.addFragment(new CurrentFragment());
        pagerAdapter.addFragment(new FavouriteFragment());
    }

    private void addFragmentTitles(MovieCollectionPagerAdapter pagerAdapter) {
        pagerAdapter.addFragmentTitle(TabTitles.POPULAR_MOVIES);
        pagerAdapter.addFragmentTitle(TabTitles.TOP_MOVIES);
        pagerAdapter.addFragmentTitle(TabTitles.CURRENT_MOVIES);
        pagerAdapter.addFragmentTitle(TabTitles.FAVOURITE_MOVIES);
    }

}
