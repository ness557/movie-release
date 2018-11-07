package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.DiscoverSearchCriteria;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearchWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.search.TVSearchWrapper;

import java.util.Optional;

public interface DiscoverService {
    Optional<MovieSearchWrapper> discoverMovie(DiscoverSearchCriteria criteria);
    Optional<TVSearchWrapper> discoverSeries(DiscoverSearchCriteria criteria);
}
