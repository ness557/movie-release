package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> getGenres(Language language);
}
