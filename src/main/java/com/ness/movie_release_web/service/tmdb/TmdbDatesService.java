package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import com.ness.movie_release_web.dto.tmdb.releaseDates.ReleaseType;

import java.util.List;

public interface TmdbDatesService {
    List<TmdbReleaseDate> getReleaseDates(String imdbId, ReleaseType... releaseTypes);
    List<TmdbReleaseDate> getReleaseDates(Long tmdbId, ReleaseType... releaseTypes);
    List<TmdbReleaseDate> getReleaseDates(String imdbId);
    List<TmdbReleaseDate> getReleaseDates(Long tmdbId);
}
