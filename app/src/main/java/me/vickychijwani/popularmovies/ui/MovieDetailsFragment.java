package me.vickychijwani.popularmovies.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.vickychijwani.popularmovies.R;
import me.vickychijwani.popularmovies.entity.Movie;
import me.vickychijwani.popularmovies.util.Util;

public class MovieDetailsFragment extends BaseFragment {

    @Bind(R.id.backdrop)            ImageView mBackdrop;
    @Bind(R.id.poster)              ImageView mPoster;
    @Bind(R.id.title)               TextView mTitle;
    @Bind(R.id.release_date)        TextView mReleaseDate;
    @Bind(R.id.rating)              TextView mRating;
    @Bind(R.id.rating_container)    ViewGroup mRatingContainer;
    @Bind(R.id.synopsis)            TextView mSynopsis;

    public MovieDetailsFragment() {}

    public static MovieDetailsFragment newInstance(Movie movie) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(BundleKeys.MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, view);

        Movie movie = getArguments().getParcelable(BundleKeys.MOVIE);
        if (movie == null) {
            throw new IllegalStateException("No movie given!");
        }

        Picasso picasso = Picasso.with(getActivity());

        int backdropWidth = Util.getScreenWidth(getActivity());
        int backdropHeight = getResources().getDimensionPixelSize(R.dimen.details_backdrop_height);
        picasso.load(Util.buildBackdropUrl(movie.getBackdropPath(), backdropWidth))
                .resize(backdropWidth, backdropHeight)
                .centerCrop()
                .transform(PaletteTransformation.instance())
                .into(mBackdrop, new ActivityPaletteTransformation(mBackdrop));

        int posterWidth = getResources().getDimensionPixelSize(R.dimen.details_poster_width);
        int posterHeight = getResources().getDimensionPixelSize(R.dimen.details_poster_height);
        picasso.load(Util.buildPosterUrl(movie.getPosterPath(), posterWidth))
                .resize(posterWidth, posterHeight)
                .centerCrop()
                .into(mPoster);

        mTitle.setText(movie.getTitle());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(movie.getReleaseDate());
        mReleaseDate.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        mRating.setText(String.format("%1$2.1f", movie.getRating()));
        mSynopsis.setText(movie.getSynopsis());

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                animateContent();
                return true;
            }
        });

        return view;
    }

    public void animateContent() {
        View[] animatedViews = new View[] {
                mTitle, mReleaseDate, mRatingContainer, mSynopsis
        };
        Interpolator interpolator = new DecelerateInterpolator();
        for (int i = 0; i < animatedViews.length; ++i) {
            View v = animatedViews[i];
            v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            v.setAlpha(0f);
            v.setTranslationY(75);
            v.animate()
                    .setInterpolator(interpolator)
                    .alpha(1.0f)
                    .translationY(0)
                    .setStartDelay(100 + 75 * i)
                    .start();
        }
    }

    class ActivityPaletteTransformation extends PaletteTransformation.Callback {
        public ActivityPaletteTransformation(@NonNull ImageView imageView) {
            super(imageView);
        }

        @Override
        protected void onSuccess(Palette palette) {
            Activity activity = getActivity();
            if (activity instanceof PaletteCallback) {
                PaletteCallback callback = (PaletteCallback) activity;
                callback.setPrimaryColor(palette);
            }
        }

        @Override
        public void onError() {}
    }

    public interface PaletteCallback {
        void setPrimaryColor(Palette palette);
    }

}