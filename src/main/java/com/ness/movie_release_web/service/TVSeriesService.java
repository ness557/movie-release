package com.ness.movie_release_web.service;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.Mode;
import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.dto.tmdb.tvSeries.WatchStatus;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.Status;
import com.ness.movie_release_web.dto.tvseries.*;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.repository.TVSeriesSortBy;

import java.util.List;

public interface TVSeriesService {

    TvSeriesDto getShow(Long tmdbId, Language language, Mode mode, String login);

    TvSeriesSeasonDto getSeason(Long tmdbId, Long seasonNumber, Language language, String login);

    TvSeriesSearchDto search(String query, Long year, Long page, Language language, String login);

    TvSeriesSubscriptionsDto getSubscriptions(List<Status> statuses,
                                              List<WatchStatus> watchStatuses,
                                              TVSeriesSortBy sort,
                                              Boolean viewMode,
                                              Long page,
                                              String login,
                                              Language language);

    TvSeriesDiscoverDto discover(TmdbDiscoverSearchCriteria criteria, String login);

    void setSeasonAndEpisode(Long tmdbId, User user, Long seasonNum, Long episodeNum);

    void updateDB();

}