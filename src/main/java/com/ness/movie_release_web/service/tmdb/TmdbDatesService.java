package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDateWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseType;

import java.util.List;

public interface TmdbDatesService {
    List<ReleaseDateWrapper> getReleaseDates(String imdbId, ReleaseType... releaseTypes);
    List<ReleaseDateWrapper> getReleaseDates(Integer tmdbId, ReleaseType... releaseTypes);
    List<ReleaseDateWrapper> getReleaseDates(String imdbId);
    List<ReleaseDateWrapper> getReleaseDates(Integer tmdbId);
}
