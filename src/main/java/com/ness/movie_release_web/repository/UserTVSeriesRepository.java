package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.UserTVSeriesPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserTVSeriesRepository extends JpaRepository<UserTVSeries, UserTVSeriesPK> {

}
