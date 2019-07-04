package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.TVSeries;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.Status;
import com.ness.movie_release_web.repository.TVSeriesSortBy;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface TVSeriesService {

    Boolean isExistsByTmdbIdAndUserId(Long tmdbId, Long userId);

    Optional<UserTVSeries> getByTmdbIdAndUserId(Long tmdbId, Long userId);

    Page<UserTVSeries> getByUserAndTVStatusesAndWatchStatusesWithOrderAndPages(List<Status> tvStatuses,
                                                                               List<WatchStatus> watchStatuses,
                                                                               TVSeriesSortBy sortBy,
                                                                               User user,
                                                                               Integer page,
                                                                               Integer size);

    List<UserTVSeries> getAllUserTVSeries();

    void setSeasonAndEpisode(Long tmdbId, User user, Long seasonNum, Long episodeNum);

    Long spentTotalMinutesToSeries(Long tmdbId, User user, Long currentSeason, Long currentEpisode);

    Long spentTotalMinutesToSeriesSeason(Long tmdbId, Long season, User user, Long currentSeason, Long currentEpisode);

    void updateDB();

    Optional<TVSeries> findById(Long id);
}