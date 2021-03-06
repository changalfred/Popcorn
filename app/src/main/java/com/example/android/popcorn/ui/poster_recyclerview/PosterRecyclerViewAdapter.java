package com.example.android.popcorn.ui.poster_recyclerview;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.android.popcorn.R;
import com.example.android.popcorn.model.Movie;
import com.example.android.popcorn.ui.GlideApp;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popcorn.NullChecker.isNotNullPath;
import static com.example.android.popcorn.Utilities.convertDoubleToString;
import static com.example.android.popcorn.Utilities.hasAtLeastOneGenre;
import static com.example.android.popcorn.Utilities.roundToNearestTenth;
import static com.example.android.popcorn.ui.ColourFiller.hasSwatch;

/**
 * Created by alfredchang on 2017-09-21.
 */

public class PosterRecyclerViewAdapter extends RecyclerView.Adapter<PosterRecyclerViewAdapter.PosterViewHolder> {

    private final String LOG_TAG = PosterRecyclerViewAdapter.class.getSimpleName();

    // Approximate dimensions.
    private final int POSTER_WIDTH = 700;
    private final int POSTER_HEIGHT = 800;
    private final int CROSSFADE_TIME = 800;
    private final int FIRST_GENRE = 0;

    private final int ROTATION_FROM_DEGREES = 0;
    private final int ROTATION_TO_DEGREES = 360;
    private final int ROTATION_TO_DEGREES_REVERSE = -360;
    private final int ANIMATION_DURATION = 500;
    private final float PIVOT_X = 0.5f;
    private final float PIVOT_Y = 0.5f;

    private final int FIRST_ITEM = 0;
    private final int LIST_SIZE = 20;
    private final String NO_GENRE = "No genre";
    private final String SNACKBAR_SAVE_MESSAGE = "Saved to favourites!";
    private final String SNACKBAR_UNSAVE_MESSAGE = "Removed from favourites!";
    private final String SNACKBAR_ACTION_MESSAGE = "Dismiss";

    private Context mContext;
    private List<Movie> mListOfMovies;
    private OnMovieClickListener mClickListener;
    private OnMovieLongClickListener mLongClickListener;

    public PosterRecyclerViewAdapter(List<Movie> listOfMovies, OnMovieClickListener clickListener,
                                     OnMovieLongClickListener longClickListener) {
        mListOfMovies = listOfMovies;
        mClickListener = clickListener;
        mLongClickListener = longClickListener;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Source: https://classroom.udacity.com/courses/ud851/lessons/
        // c81cb722-d20a-495a-83c6-6890a6142aac/concepts/ae70fe56-dbd3-446c-be43-b8da0f076ea6.
        mContext = parent.getContext();
        int movieLayoutId = R.layout.movie_poster;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(movieLayoutId, parent, false);
        PosterViewHolder posterViewHolder = new PosterViewHolder(view);

        return posterViewHolder;
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        Movie movie = mListOfMovies.get(position);
        onBindPoster(movie, holder);
        onBindTitle(movie, holder);
        onBindRating(movie, holder);
        onBindGenres(movie, holder);
    }

    // The GlideApp.method(...) can't be moved to ViewPopulator.java because it requires a holder
    // parameter, which can't be used in other classes.
    private void onBindPoster(Movie movie, final PosterViewHolder holder) {
        if (isNotNullPath(movie)) {
            GlideApp.with(mContext).load(movie.getPosterPath())
                    .listener(GlidePalette.with(movie.getPosterPath())
                            .intoCallBack(new GlidePalette.CallBack() {
                                @Override
                                public void onPaletteLoaded(Palette palette) {
                                    onBindColorToCardView(holder, palette.getSwatches());
                                }
                            })
                    )
                    .override(POSTER_WIDTH, POSTER_HEIGHT)
                    .placeholder(R.drawable.poster_placeholder)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade(CROSSFADE_TIME))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(holder.mPoster);
        }
    }

    private void onBindColorToCardView(PosterViewHolder holder, List<Palette.Swatch> swatches) {
        for (Palette.Swatch swatch : swatches) {
            if (hasSwatch(swatch)) {
                holder.mLinearLayout.setBackgroundColor(swatch.getRgb());
                holder.mTitle.setTextColor(swatch.getTitleTextColor());
                holder.mRating.setTextColor(swatch.getBodyTextColor());
                holder.mGenres.setTextColor(swatch.getBodyTextColor());
            }
        }
    }

    private void onBindTitle(Movie movie, PosterViewHolder holder) {
        holder.mTitle.setText(movie.getTitle());
    }

    private void onBindRating(Movie movie, PosterViewHolder holder) {
        double rating = roundToNearestTenth(movie.getRating());
        String ratingAsString = convertDoubleToString(rating);
        holder.mRating.setText(mContext.getResources().getString(R.string.rating_out_of_ten, ratingAsString));
    }

    private void onBindGenres(Movie movie, PosterViewHolder holder) {
        List<String> genres = movie.getGenres();
        if (hasAtLeastOneGenre(genres)) {
            holder.mGenres.setText(genres.get(FIRST_GENRE));
        } else {
            holder.mGenres.setText(NO_GENRE);
        }
    }

    // Call this when refreshing layout.
    public void clearData() {
        mListOfMovies.clear();
        notifyDataSetChanged();
    }

    // Call this when refreshing layout.
    public void renewData(List<Movie> listOfMovies) {
        mListOfMovies.addAll(listOfMovies);
        notifyDataSetChanged();
    }

    public void renewDataAfterSort(List<Movie> listOfMovies) {
        mListOfMovies.clear();
        mListOfMovies.addAll(listOfMovies);

        // TODO: notifyItemRangeChanged(...).
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mListOfMovies.size();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        @BindView(R.id.movie_poster)
        ImageView mPoster;
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.rating)
        TextView mRating;
        @BindView(R.id.genres)
        TextView mGenres;
        @BindView(R.id.unbookmark)
        ImageButton mUnbookmark;
        @BindView(R.id.bookmark)
        ImageButton mBookmark;
        @BindView(R.id.linear_layout)
        LinearLayout mLinearLayout;

        public PosterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            onClickBookmark();
        }

        @Override
        public void onClick(View view) {
            mClickListener.onClick(mListOfMovies.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View view) {
            mLongClickListener.onLongClick(mListOfMovies.get(getAdapterPosition()));
            return true;
        }

        private void displaySnackbar(String snackbarMessage) {
            final Snackbar snackbar = Snackbar.make(mLinearLayout, snackbarMessage, Snackbar.LENGTH_LONG)
                    .setActionTextColor(ContextCompat.getColor(mContext, R.color.red));
            snackbar.setAction(SNACKBAR_ACTION_MESSAGE, new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });

            snackbar.show();
        }

        private void onClickBookmarkResult(ImageButton bookmarkIconOne, ImageButton bookmarkIconTwo,
                                           float rotationToDegrees) {
            if (bookmarkIconTwo.getVisibility() == View.GONE) {
                bookmarkIconOne.setVisibility(View.GONE);
                bookmarkIconTwo.setVisibility(View.VISIBLE);

                RotateAnimation rotation = new RotateAnimation(ROTATION_FROM_DEGREES, rotationToDegrees,
                        Animation.RELATIVE_TO_SELF, PIVOT_X,
                        Animation.RELATIVE_TO_SELF, PIVOT_Y);
                rotation.setDuration(ANIMATION_DURATION);
                bookmarkIconTwo.startAnimation(rotation);
            }
        }

        private void onClickBookmark() {
            mUnbookmark.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    displaySnackbar(SNACKBAR_SAVE_MESSAGE);
                    onClickBookmarkResult(mUnbookmark, mBookmark, ROTATION_TO_DEGREES);
                }
            });

            mBookmark.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    displaySnackbar(SNACKBAR_UNSAVE_MESSAGE);
                    onClickBookmarkResult(mBookmark, mUnbookmark, ROTATION_TO_DEGREES_REVERSE);
                }
            });
        }
    }
}
