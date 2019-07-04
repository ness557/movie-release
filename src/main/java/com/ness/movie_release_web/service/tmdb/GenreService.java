package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.GenreDto;
import com.ness.movie_release_web.model.dto.tmdb.Language;

import java.util.List;

public interface GenreService {
    List<GenreDto> getMovieGenres(Language language);
    List<GenreDto> getTVGenres(Language language);
}
