package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.TVSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface TVSeriesRepository extends JpaRepository<TVSeries, Long> {

}
