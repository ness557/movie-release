package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.OmdbFullWrapper;
import com.ness.movie_release_web.model.OmdbSearchResultWrapper;

public interface FilmOmdbService {

    OmdbSearchResultWrapper search(String title, Integer year, Integer page);
    OmdbFullWrapper getInfo(String imdbId);
}
