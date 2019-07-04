package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.EpisodeDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.SeasonDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.TVDetailsDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.search.TVSearchDto;

import java.util.Optional;

public interface TmdbTVSeriesService {
    Optional<TVDetailsDto> getTVDetails(Long tmdbId, Language language);

    Optional<SeasonDto> getSeasonDetails(Long tmdbId, Long season, Language language);
    Optional<EpisodeDto> getEpisodeDetails(Long tmdbId, Long season, Long episode, Language language);

    Optional<TVSearchDto> search(String query, Integer page, Long year, Language language);
}
