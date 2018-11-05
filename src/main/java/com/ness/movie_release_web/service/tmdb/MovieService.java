package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearchWrapper;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface MovieService {
    Optional<MovieDetailsWrapper> getMovieDetails(Integer tmdbId, Language language);
    Optional<MovieSearchWrapper> searchForMovies(String query, @Nullable Integer page, @Nullable Integer year, Language language);
}
