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

    //    TODO
//    (u) current season and episode (u) episode watched or not
    Long spentTotalMinutesToSeries(Integer tmdbId, User user);
}