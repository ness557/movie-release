package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDate;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseType;

import java.util.List;

public interface TmdbDatesService {
    List<ReleaseDate> getReleaseDates(String imdbId, ReleaseType... releaseTypes);
    List<ReleaseDate> getReleaseDates(Long tmdbId, ReleaseType... releaseTypes);
    List<ReleaseDate> getReleaseDates(String imdbId);
    List<ReleaseDate> getReleaseDates(Long tmdbId);
}
