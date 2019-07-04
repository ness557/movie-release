package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.MovieDetailsDto;
import com.ness.movie_release_web.model.dto.tmdb.movie.search.MovieSearchDto;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface TmdbMovieService {
    Optional<MovieDetailsDto> getMovieDetails(Long tmdbId, Language language);
    Optional<MovieSearchDto> searchForMovies(String query, @Nullable Integer page, @Nullable Long year, Language language);
}
