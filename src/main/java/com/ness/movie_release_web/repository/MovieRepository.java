package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.Movie;
import com.ness.movie_release_web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    Optional<Movie> findByIdAndUsers(Long id, User user);

    boolean existsByIdAndUsers(Long id, User user);

    List<Movie> findAllByUsersIsNotEmpty();

}
