package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.movie.discover.DiscoverSearchCriteria;
import com.ness.movie_release_web.model.dto.tmdb.movie.search.MovieSearchDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.search.TVSearchDto;

import java.util.Optional;

public interface DiscoverService {
    Optional<MovieSearchDto> discoverMovie(DiscoverSearchCriteria criteria);
    Optional<TVSearchDto> discoverSeries(DiscoverSearchCriteria criteria);
}
