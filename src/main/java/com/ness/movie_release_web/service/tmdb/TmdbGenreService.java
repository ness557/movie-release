package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.tmdb.TmdbGenreDto;
import com.ness.movie_release_web.dto.Language;

import java.util.List;

public interface TmdbGenreService {
    List<TmdbGenreDto> getMovieGenres(Language language);
    List<TmdbGenreDto> getTVGenres(Language language);
}
