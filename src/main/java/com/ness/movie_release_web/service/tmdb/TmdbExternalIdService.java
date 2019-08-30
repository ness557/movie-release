package com.ness.movie_release_web.service.tmdb;

public interface TmdbExternalIdService {

    Long getTmdbIdByImdbId(String imdbId);
}
