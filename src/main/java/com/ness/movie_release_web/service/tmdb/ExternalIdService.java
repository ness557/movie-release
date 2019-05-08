package com.ness.movie_release_web.service.tmdb;

public interface ExternalIdService {

    Long getTmdbIdByImdbId(String imdbId);
}
