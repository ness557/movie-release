package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.UserTVSeriesPK;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserTVSeriesRepository extends JpaRepository<UserTVSeries, UserTVSeriesPK> {

    Page<UserTVSeries> findAllByUserOrderByTvSeriesIdDesc(User user, Pageable pageable);

    Page<UserTVSeries> findAllByUserOrderByTvSeriesNameEnDesc(User user, Pageable pageable);
    Page<UserTVSeries> findAllByUserOrderByTvSeriesNameEnAsc(User user, Pageable pageable);
    Page<UserTVSeries> findAllByUserOrderByTvSeriesNameRuDesc(User user, Pageable pageable);
    Page<UserTVSeries> findAllByUserOrderByTvSeriesNameRuAsc(User user, Pageable pageable);
    Page<UserTVSeries> findAllByUserOrderByTvSeriesReleaseDateDesc(User user, Pageable pageable);
    Page<UserTVSeries> findAllByUserOrderByTvSeriesReleaseDateAsc(User user, Pageable pageable);
    Page<UserTVSeries> findAllByUserOrderByTvSeriesLastEpisodeAirDateDesc(User user, Pageable pageable);
    Page<UserTVSeries> findAllByUserOrderByTvSeriesLastEpisodeAirDateAsc(User user, Pageable pageable);
    Page<UserTVSeries> findAllByUserOrderByTvSeriesVoteAverageDesc(User user, Pageable pageable);
    Page<UserTVSeries> findAllByUserOrderByTvSeriesVoteAverageAsc(User user, Pageable pageable);

    Page<UserTVSeries> findAllByUserAndTvSeriesStatus(User user, List<Status> statuses, Pageable pageable);
}
