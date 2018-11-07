package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.GenreWrapper;

import java.util.List;

public interface GenreService {
    List<GenreWrapper> getMovieGenres(Language language);
    List<GenreWrapper> getTVGenres(Language language);
}
