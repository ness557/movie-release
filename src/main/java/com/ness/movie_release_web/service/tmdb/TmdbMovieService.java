package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieSearchDto;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface TmdbMovieService {
    Optional<TmdbMovieDetailsDto> getMovieDetails(Long tmdbId, Language language);
    Optional<TmdbMovieSearchDto> searchForMovies(String query, @Nullable Long page, @Nullable Long year, Language language);
}
