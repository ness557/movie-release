package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.SortBy;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearch;

import java.util.Optional;

public interface DiscoverService {
    Optional<MovieSearch> searchByGenre(Integer genreId, Integer page, SortBy sortBy, Language language);
}
