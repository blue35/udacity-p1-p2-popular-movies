package me.vickychijwani.popularmovies.util;

import android.net.Uri;
import android.util.Log;

import me.vickychijwani.popularmovies.BuildConfig;

public class TMDbUtil {

    private static final Uri TMDB_IMAGE_BASE_URI = Uri.parse("http://image.tmdb.org/t/p/");

    private interface TMDbImageWidth {
        String getWidthString();
        int getMaxWidth();
    }

    public enum TMDbPosterWidth implements TMDbImageWidth {
        W92(92), W154(154), W185(185), W342(342), W500(500), W780(780), ORIGINAL(Integer.MAX_VALUE);

        public final int maxWidth;
        TMDbPosterWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }
        public int getMaxWidth() {
            return this.maxWidth;
        }
        public String getWidthString() {
            return (this == ORIGINAL) ? "original" : "w" + this.maxWidth;
        }
    }

    public enum TMDbBackdropWidth implements TMDbImageWidth {
        W300(300), W780(780), W1280(1280), ORIGINAL(Integer.MAX_VALUE);

        public final int maxWidth;
        TMDbBackdropWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }
        public int getMaxWidth() {
            return this.maxWidth;
        }
        public String getWidthString() {
            return (this == ORIGINAL) ? "original" : "w" + this.maxWidth;
        }
    }

    public static String buildPosterUrl(String posterPath, int posterWidth) {
        return buildImageUrl(posterPath, computeNextLowestPosterWidth(posterWidth));
    }

    public static String buildBackdropUrl(String backdropPath, int backdropWidth) {
        return buildImageUrl(backdropPath, computeNextLowestBackdropWidth(backdropWidth));
    }

    private static <T extends TMDbImageWidth> String buildImageUrl(String imagePath, T tmdbImageWidth) {
        if (BuildConfig.DEBUG) {
            Log.v("Picasso", "Loading image of width " + tmdbImageWidth.getMaxWidth() + "px");
        }
        String relativePath = tmdbImageWidth.getWidthString() + "/" + imagePath;
        return Uri.withAppendedPath(TMDB_IMAGE_BASE_URI, relativePath).toString();
    }


    // private methods

    // 50 => W92, 92 => W92, 93 => W185, 999 => ORIGINAL
    private static TMDbPosterWidth computeNextLowestPosterWidth(int posterWidth) {
        for (TMDbPosterWidth enumWidth : TMDbPosterWidth.values()) {
            if (0.8 * posterWidth <= enumWidth.maxWidth) {
                return enumWidth;
            }
        }
        return TMDbPosterWidth.ORIGINAL;
    }

    private static TMDbBackdropWidth computeNextLowestBackdropWidth(int backdropWidth) {
        for (TMDbBackdropWidth enumWidth : TMDbBackdropWidth.values()) {
            if (0.8 * backdropWidth <= enumWidth.maxWidth) {
                return enumWidth;
            }
        }
        return TMDbBackdropWidth.ORIGINAL;
    }

}
