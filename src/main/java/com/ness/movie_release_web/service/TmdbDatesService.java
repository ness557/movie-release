package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.wrapper.tmdbReleaseDates.ReleaseDate;
import com.ness.movie_release_web.model.wrapper.tmdbReleaseDates.ReleaseType;

import java.util.List;

public interface TmdbDatesService {
    List<ReleaseDate> getReleaseDates(String imdbId, ReleaseType... releaseTypes);
    List<ReleaseDate> getReleaseDates(String imdbId);
}
