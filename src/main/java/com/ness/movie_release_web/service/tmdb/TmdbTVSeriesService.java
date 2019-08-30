package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVSearchDto;

import java.util.Optional;

public interface TmdbTVSeriesService {
    Optional<TmdbTVDetailsDto> getTVDetails(Long tmdbId, Language language);

    Optional<TmdbSeasonDto> getSeasonDetails(Long tmdbId, Long season, Language language);
    Optional<TmdbEpisodeDto> getEpisodeDetails(Long tmdbId, Long season, Long episode, Language language);

    Optional<TmdbTVSearchDto> search(String query, Integer page, Long year, Language language);
}
