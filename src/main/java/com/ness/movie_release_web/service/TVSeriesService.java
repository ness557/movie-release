package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.Status;
import com.ness.movie_release_web.repository.TVSeriesSortBy;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface TVSeriesService {

    Boolean isExistsByTmdbIdAndUserId(Integer tmdbId, Long userId);

    Optional<UserTVSeries> getByTmdbIdAndUserId(Integer tmdbId, Long userId);

    void subscribeUser(Integer tmdbId, User user);

    void unSubscribeUser(Integer tmdbId, User user);

    Page<UserTVSeries> getByUserAndTVStatusesAndWatchStatusesWithOrderAndPages(List<Status> tvStatuses,
                                                                               List<WatchStatus> watchStatuses,
                                                                               TVSeriesSortBy sortBy,
                                                                               User user,
                                                                               Integer page,
                                                                               Integer size);

    List<UserTVSeries> getAllUserTVSeries();

    void setSeasonAndEpisode(Integer tmdbId, User user, Integer seasonNum, Integer episodeNum);

    Long spentTotalMinutesToSeries(Integer tmdbId, User user, Integer currentSeason, Integer currentEpisode);

    Long spentTotalMinutesToSeriesSeason(Integer tmdbId, Integer season, User user, Integer currentSeason, Integer currentEpisode);

    void updateDB();
}