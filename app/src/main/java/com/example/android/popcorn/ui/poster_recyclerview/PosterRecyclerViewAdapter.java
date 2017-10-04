package com.example.android.popcorn.ui.poster_recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popcorn.R;
import com.example.android.popcorn.model.Movie;
import com.example.android.popcorn.ui.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.popcorn.Utilities.convertDoubleToString;
import static com.example.android.popcorn.Utilities.hasAtLeastOneGenre;
import static com.example.android.popcorn.Utilities.hasPosterPath;
import static com.example.android.popcorn.Utilities.roundToNearestTenth;

/**
 * Created by alfredchang on 2017-09-21.
 */

public class PosterRecyclerViewAdapter extends RecyclerView.Adapter<PosterRecyclerViewAdapter.PosterViewHolder> {

    private final String LOG_TAG = PosterRecyclerViewAdapter.class.getSimpleName();

    // Approximate dimensions.
    private final int POSTER_WIDTH = 700;
    private final int POSTER_HEIGHT = 800;
    private final int FIRST_GENRE = 0;
    private final String NO_GENRE = "No genre";
    private final String NO_RATING = "-/10";

    private Context mContext;
    private List<Movie> mListOfMovies;
    private OnMovieClickListener mClickListener;

    public PosterRecyclerViewAdapter(List<Movie> listOfMovies, OnMovieClickListener clickListener) {
        mListOfMovies = listOfMovies;
        mClickListener = clickListener;
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

    private void onBindPoster(Movie movie, PosterViewHolder holder) {
        if (hasPosterPath(movie)) {
            GlideApp.with(mContext).load(movie.getPosterPath()).override(POSTER_WIDTH, POSTER_HEIGHT)
                    .into(holder.mPoster);
        }
    }

    private void onBindTitle(Movie movie, PosterViewHolder holder) {
        holder.title.setText(movie.getTitle());
    }

    private void onBindRating(Movie movie, PosterViewHolder holder) {
        double rating = roundToNearestTenth(movie);
        String ratingAsString = convertDoubleToString(rating);
        holder.rating.setText(mContext.getResources().getString(R.string.rating_out_of_ten, ratingAsString));
    }

    private void onBindGenres(Movie movie, PosterViewHolder holder) {
        List<String> genres = movie.getGenres();
        if (hasAtLeastOneGenre(genres)) {
            holder.genres.setText(genres.get(FIRST_GENRE));
        } else {
            holder.genres.setText(NO_GENRE);
        }
    }

    @Override
    public int getItemCount() {
        return mListOfMovies.size();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.movie_poster) ImageView mPoster;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.rating) TextView rating;
        @BindView(R.id.genres) TextView genres;

        public PosterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onMovieClick(mListOfMovies.get(getAdapterPosition()));
        }
    }
}