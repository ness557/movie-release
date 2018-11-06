package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import org.springframework.data.domain.Page;

public interface TVSeriesService {

    Boolean isExistsByTmdbIdAndUserId(Integer tmdbId, Long userId);

    void subscribeUser(Integer tmdbId, User user);
    void unSubscribeUser(Integer tmdbId, User user);

    Page<UserTVSeries> getAllByUserWithPages(Integer page, Integer size, User user);
//    TODO
//    (u) current season and episode (u) episode watched or not
//    (u) total time spent to tv series
}