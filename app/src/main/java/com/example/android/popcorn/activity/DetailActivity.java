package com.example.android.popcorn.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popcorn.MainActivity;
import com.example.android.popcorn.R;
import com.example.android.popcorn.Utilities;
import com.example.android.popcorn.data.DbContract;
import com.example.android.popcorn.data.DbContract.SavedMoviesEntry;
import com.example.android.popcorn.data.DbContract.TrailersEntry;
import com.example.android.popcorn.data.DbHelper;
import com.example.android.popcorn.fragment.CastFragment;
import com.example.android.popcorn.fragment.DetailFragment;
import com.example.android.popcorn.fragment.ReviewFragment;
import com.example.android.popcorn.model.Movie;
import com.example.android.popcorn.networking.UrlCreator;
import com.example.android.popcorn.ui.DetailTabsPagerAdapter;
import com.example.android.popcorn.ui.TabTitles;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popcorn.Utilities.PARENT_ACTIVITY;
import static com.example.android.popcorn.data.DbHelper.getDbInstance;
import static com.example.android.popcorn.model.singleton.SavedMoviesSingleton.getSavedMoviesSingleton;
import static com.example.android.popcorn.ui.LayoutPropertiesInitializer.initImageViewProperties;
import static com.example.android.popcorn.ui.ViewPopulator.populateCenterCropImageView;
import static com.example.android.popcorn.ui.ViewPopulator.populateDateToTextView;
import static com.example.android.popcorn.ui.ViewPopulator.populateImageViewWithCrossFade;
import static com.example.android.popcorn.ui.ViewPopulator.populateRatingTextView;
import static com.example.android.popcorn.ui.ViewPopulator.populateRuntimeTextView;
import static com.example.android.popcorn.ui.ViewPopulator.populateStringListToTextView;
import static com.example.android.popcorn.ui.ViewPopulator.populateTextView;



/**
 * Created by alfredchang on 2017-09-27.
 */

// Extend AppCompatActivity for back button layout.
public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    private final int BACKDROP_CROSSFADE_TIME = 300;
    private final int POSTER_CROSSFADE_TIME = 500;
    private final int PAGES_TO_RETAIN = 2;
    private final int FIRST_GENRE = 0;
    private final String SAVED = "Saved to favourites!";
    private final String UNSAVED = "Removed from favourites!";

    private SQLiteDatabase mSqlDb;
    private DbHelper mDbHelper;
    private Cursor mCursor;
    private Set<Movie> setOfSavedMovies;
    private Movie mMovie;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.backdrop_poster) ImageView mBackdrop;
    @BindView(R.id.movie_poster) ImageView mPoster;
    @BindView(R.id.poster_background) ImageView mPosterBackground;
    @BindView(R.id.app_bar) AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.detail_tabs) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.tmdb_branding) ImageView mTmdbBranding;
    @BindView(R.id.favourite_fab) FloatingActionButton mFavouriteButton;

    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.rating) TextView mRating;
    @BindView(R.id.runtime) TextView mRuntime;
    @BindView(R.id.release) TextView mRelease;
    @BindView(R.id.genres) TextView mGenres;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mMovie = getParcelableMovieDetails();
        setOfSavedMovies = getSavedMoviesSingleton();
        setParcelableDetailsIntoViews(mMovie);
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDbHelper = getDbInstance(this);
        mSqlDb = mDbHelper.getWritableDatabase();
        mCursor = getSavedMoviesTable();

        displayTitleOnCollapsedToolbar(mMovie);
        initFab();
        setClickListenerFab();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSqlDb.close();
        mCursor.close();
    }

    private Cursor getSavedMoviesTable() {
        return mSqlDb.query(
                DbContract.SavedMoviesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private long addDetailToDbTable() {
        return addSavedMovieDetails();
    }

    private long addCastToDbTable() {
        return addSavedMovieCast();
    }

    private long addReviewToDbTable() {
        return addSavedMovieReview();
    }

    private long addTrailersToDbTable() {
        return addSavedTrailer();
    }

    private long addSavedMovieDetails() {
        ContentValues cv = new ContentValues();

        cv.put(SavedMoviesEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
        cv.put(SavedMoviesEntry.COLUMN_TITLE, mMovie.getTitle());
        cv.put(SavedMoviesEntry.COLUMN_RATING, mMovie.getRating());
        cv.put(SavedMoviesEntry.COLUMN_GENRES, mMovie.getGenres().get(FIRST_GENRE));
        cv.put(SavedMoviesEntry.COLUMN_BACKDROP_PATH, mMovie.getBackdropPath());
        cv.put(SavedMoviesEntry.COLUMN_RUNTIME, mMovie.getRuntime());
        cv.put(SavedMoviesEntry.COLUMN_RELEASE, mMovie.getReleaseDate());
        cv.put(SavedMoviesEntry.COLUMN_TAGLINE, mMovie.getTagline());
        cv.put(SavedMoviesEntry.COLUMN_OVERVIEW, mMovie.getOverview());
        cv.put(SavedMoviesEntry.COLUMN_DIRECTOR_PHOTO_PATH, mMovie.getDirector().getProfilePath());
        cv.put(SavedMoviesEntry.COLUMN_DIRECTOR_NAME, mMovie.getDirector().getName());
        cv.put(SavedMoviesEntry.COLUMN_PRODUCER_PHOTO_PATH, mMovie.getProducer().getProfilePath());
        cv.put(SavedMoviesEntry.COLUMN_PRODUCER_NAME, mMovie.getProducer().getName());
        cv.put(SavedMoviesEntry.COLUMN_LANGUAGES, Utilities.convertListToString(mMovie.getLanguages()));
        cv.put(SavedMoviesEntry.COLUMN_BUDGET, mMovie.getBudget());
        cv.put(SavedMoviesEntry.COLUMN_REVENUE, mMovie.getRevenue());
        cv.put(SavedMoviesEntry.COLUMN_PROD_COMPANIES, Utilities.convertListToString(mMovie.getProductionCompanies()));

        return mSqlDb.insertWithOnConflict(SavedMoviesEntry.TABLE_NAME,
                null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // TODO.
    private long addSavedMovieCast() {
        return 0;
    }

    // TODO.
    private long addSavedMovieReview() {
        return 0;
    }

    private long addSavedTrailer() {
        long id = -1;
        ContentValues cv = new ContentValues();

        if (mMovie.getTrailers().size() >= 1) {
            cv.put(TrailersEntry.COLUMN_KEY, mMovie.getTrailers().get(0).getKey());
            cv.put(TrailersEntry.COLUMN_DETAIL, mMovie.getTrailers().get(0).getTrailerDescription());
            id = mSqlDb.insertWithOnConflict(TrailersEntry.TABLE_NAME, null, cv,
                    SQLiteDatabase.CONFLICT_REPLACE);
        }

        return id;
    }

    public boolean removeDetailFromDbTable(long rowId) {
        return mSqlDb.delete(DbContract.SavedMoviesEntry.TABLE_NAME,
                DbContract.SavedMoviesEntry._ID + "=" + rowId, null) > 0;
    }

    public boolean removeTrailerFromDbTable(long rowId) {
        return mSqlDb.delete(DbContract.TrailersEntry.TABLE_NAME,
                DbContract.SavedMoviesEntry._ID + "=" + rowId, null) > 0;
    }

    public void displayTitleOnCollapsedToolbar(final Movie movie) {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int diffRangeAndOffset = scrollRange + verticalOffset;

                if (scrollRange == -1) {
                    scrollRange = mAppBarLayout.getTotalScrollRange();
                }

                if (diffRangeAndOffset == 0) {
                    mCollapsingToolbarLayout.setTitle(movie.getTitle());
                } else {
                    mCollapsingToolbarLayout.setTitle(" ");
                }
            }
        });
    }

    private void initFab() {
        // If a movie is already saved, choose right heart image.
        if (isAlreadyLiked()) {
            mFavouriteButton.setImageResource(R.mipmap.ic_favourited);
        }
    }

    private void setClickListenerFab() {
        mFavouriteButton.setOnClickListener(new View.OnClickListener() {
            private long rowIdDetail = -1;
            private long rowIdTrailer = -1;
            private long rowIdCast = -1;
            private long rowIdReview = -1;
            private boolean isLiked = false;

            @Override
            public void onClick(View view) {
                if (!isLiked) {
                    rowIdDetail = addDetailToDbTable();
                    rowIdTrailer = addTrailersToDbTable();
                    rowIdCast = addCastToDbTable();
                    rowIdReview = addReviewToDbTable();
                    setOfSavedMovies.add(mMovie);
                    mFavouriteButton.setImageResource(R.mipmap.ic_favourited);
                    Toast.makeText(getApplicationContext(), SAVED, Toast.LENGTH_SHORT).show();
                    isLiked = true;
                } else {
                    removeDetailFromDbTable(rowIdDetail);
                    removeTrailerFromDbTable(rowIdTrailer);
                    setOfSavedMovies.remove(mMovie);
                    mFavouriteButton.setImageResource(R.mipmap.ic_favourite);
                    Toast.makeText(getApplicationContext(), UNSAVED, Toast.LENGTH_SHORT).show();
                    isLiked = false;
                }
            }
        });
    }

    private boolean isAlreadyLiked() {
        boolean isLiked = false;
        String queryString = "SELECT * FROM " + DbContract.SavedMoviesEntry.TABLE_NAME
                + " WHERE TITLE = '" + mMovie.getTitle() + "'";
        Cursor cursor = mSqlDb.rawQuery(queryString, null);

        if (cursor.getCount() >= 1) {
            isLiked = true;
        }

        cursor.close();
        return isLiked;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.action_settings) {

        } else if (id == R.id.action_share) {
            shareItem();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public Intent getParentActivityIntent() {
        return getLastParentActivityIntent();
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        return getLastParentActivityIntent();
    }

    private Intent getLastParentActivityIntent() {
        Intent lastParentIntent;
        String parentName = getParentName();

        if (parentName == Utilities.MAIN_ACTIVITY_PARENT) {
            lastParentIntent = new Intent(this, MainActivity.class);
        } else {
            lastParentIntent = new Intent(this, SearchResultsActivity.class);
        }
        lastParentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return lastParentIntent;
    }

    private void shareItem() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, UrlCreator.createUrl(mMovie.getId()));

        Log.d(LOG_TAG, "Url created: " + UrlCreator.createUrl(mMovie.getId()));

        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_intent_title)));
    }

    private String getParentName() {
        return getIntent().getStringExtra(PARENT_ACTIVITY);
    }

    private Movie getParcelableMovieDetails() {
        Intent intent = getIntent();
        Log.d(LOG_TAG, "What is this? " + intent.getParcelableExtra(Utilities.PARCELABLE_MOVIE_KEY));
        return intent.getParcelableExtra(Utilities.PARCELABLE_MOVIE_KEY);
    }

    private void setParcelableDetailsIntoViews(Movie movie) {
        populateCenterCropImageView(initImageViewProperties(this,
                movie.getBackdropPath(), BACKDROP_CROSSFADE_TIME, mBackdrop));

        populateImageViewWithCrossFade(initImageViewProperties(this, movie.getPosterPath(),
                POSTER_CROSSFADE_TIME, mPoster, mPosterBackground, mTmdbBranding, mTitle, mRating,
                mRuntime, mRelease, mGenres, mTabLayout, mCollapsingToolbarLayout, mFavouriteButton));

        populateTextView(movie.getTitle(), mTitle);
        populateRatingTextView(this, movie.getRating(), mRating);
        populateRuntimeTextView(this, movie.getRuntime(), mRuntime);
        populateDateToTextView(movie.getReleaseDate(), mRelease, "yyyy");
        populateStringListToTextView(movie.getGenres(), mGenres);
    }

    private void setupViewPager(ViewPager viewPager) {
        DetailTabsPagerAdapter pagerAdapter = new DetailTabsPagerAdapter(getSupportFragmentManager());
        addFragments(pagerAdapter);
        addFragmentTitles(pagerAdapter);
        viewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(PAGES_TO_RETAIN);
    }

    private void addFragments(DetailTabsPagerAdapter pagerAdapter) {
        pagerAdapter.addFragment(new DetailFragment());
        pagerAdapter.addFragment(new CastFragment());
        pagerAdapter.addFragment(new ReviewFragment());
    }

    private void addFragmentTitles(DetailTabsPagerAdapter pagerAdapter) {
        pagerAdapter.addFragmentTitle(TabTitles.DETAILS);
        pagerAdapter.addFragmentTitle(TabTitles.CAST);
        pagerAdapter.addFragmentTitle(TabTitles.REVIEWS);
    }
}
