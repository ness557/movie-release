package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.EpisodeWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.SeasonWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.search.TVSearchWrapper;

import java.util.Optional;

public interface TmdbTVSeriesService {
    Optional<TVDetailsWrapper> getTVDetails(Long tmdbId, Language language);

    Optional<SeasonWrapper> getSeasonDetails(Long tmdbId, Long season, Language language);
    Optional<EpisodeWrapper> getEpisodeDetails(Long tmdbId, Long season, Long episode, Language language);

    Optional<TVSearchWrapper> search(String query, Integer page, Long year, Language language);
}
