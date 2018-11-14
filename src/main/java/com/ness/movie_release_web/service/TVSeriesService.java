package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface TVSeriesService {

    Boolean isExistsByTmdbIdAndUserId(Integer tmdbId, Long userId);

    Optional<UserTVSeries> getByTmdbIdAndUserId(Integer tmdbId, Long userId);

    void subscribeUser(Integer tmdbId, User user);

    void unSubscribeUser(Integer tmdbId, User user);

    Page<UserTVSeries> getAllByUserWithPages(Integer page, Integer size, User user);
    void setSeasonAndEpisode(Integer tmdbId, User user, Integer seasonNum, Integer episodeNum);

    Long spentTotalMinutesToSeries(Integer tmdbId, User user, Integer currentSeason, Integer currentEpisode);
    Long spentTotalMinutesToSeriesSeason(Integer tmdbId, Integer season, User user, Integer currentSeason, Integer currentEpisode);
}