package com.example.android.popcorn;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popcorn.activity.SearchResultsActivity;
import com.example.android.popcorn.fragment.CurrentFragment;
import com.example.android.popcorn.fragment.DialogFragment.OnSortByChoiceClickListener;
import com.example.android.popcorn.fragment.DialogFragment.SortByDialogFragment;
import com.example.android.popcorn.fragment.FavouriteFragment;
import com.example.android.popcorn.fragment.PopularFragment;
import com.example.android.popcorn.fragment.TopFragment;
import com.example.android.popcorn.model.Movie;
import com.example.android.popcorn.ui.MovieCollectionPagerAdapter;
import com.example.android.popcorn.ui.TabTitles;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popcorn.model.singleton.PopularMoviesSingleton.getPopularMoviesSingleton;

public class MainActivity extends AppCompatActivity implements OnSortByChoiceClickListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private final int PAGES_TO_RETAIN = 1;
    private final int SORT_BEST_TO_WORST = 0;
    private final String DIALOG_FRAGMENT = "dialog fragment";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_sort:
                SortByDialogFragment dialogFragment = new SortByDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), DIALOG_FRAGMENT);

            case R.id.action_settings:

            case R.id.action_feedback:

            case R.id.action_about:

            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(DialogFragment dialogFragment, int choice) {
        sortMovies(choice, getCurrentTab());
    }

    // Refactor this method later.
    private void sortMovies(int choice, int currentTabIndex) {
        if (choice == SORT_BEST_TO_WORST) {
            sortBestBasedOnTab(currentTabIndex);
        } else {
            sortWorstBasedOnTab(currentTabIndex);
        }
    }

    // Refactor this method later.
    private void sortBestBasedOnTab(int currentTabIndex) {
        List<Movie> listOfMovies = new ArrayList<>();
        listOfMovies.addAll(getPopularMoviesSingleton());

        for (Movie movie : listOfMovies) {
            Log.d(LOG_TAG, "Movie title and rating: " + movie.getTitle() + " " + movie.getRating());
        }
    }

    // Refactor this method later.
    private void sortWorstBasedOnTab(int currentTabIndex) {

    }

    private int getCurrentTab() {
        return mTabLayout.getSelectedTabPosition();
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
