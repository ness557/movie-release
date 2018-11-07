package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.EpisodeWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.SeasonWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.search.TVSearchWrapper;

import java.util.Optional;

public interface TmdbTVSeriesService {
    Optional<TVDetailsWrapper> getTVDetails(Integer tmdbId, Language language);

    Optional<SeasonWrapper> getSeasonDetails(Integer tmdbId, Integer season, Language language);
    Optional<EpisodeWrapper> getEpisodeDetails(Integer tmdbId, Integer season, Integer episode, Language language);

    Optional<TVSearchWrapper> search(String query, Integer page, Integer year, Language language);
}
