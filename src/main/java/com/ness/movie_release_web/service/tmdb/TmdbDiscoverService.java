package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieSearchDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVSearchDto;

import java.util.Optional;

public interface TmdbDiscoverService {
    Optional<TmdbMovieSearchDto> discoverMovie(TmdbDiscoverSearchCriteria criteria);
    Optional<TmdbTVSearchDto> discoverSeries(TmdbDiscoverSearchCriteria criteria);
}
