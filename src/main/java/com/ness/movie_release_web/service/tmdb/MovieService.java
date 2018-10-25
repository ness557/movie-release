package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetails;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearch;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface MovieService {
    Optional<MovieDetails> getMovieDetails(Integer tmdbId, Language language);
    Optional<MovieSearch> searchForMovies(String query, @Nullable Integer page, @Nullable Integer year, Language language);
}
