package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.DiscoverSearchCriteria;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearch;

import java.util.Optional;

public interface DiscoverService {
    Optional<MovieSearch> discover(DiscoverSearchCriteria criteria);
}
