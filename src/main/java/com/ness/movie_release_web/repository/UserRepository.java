package com.ness.movie_release_web.repository;

import com.ness.movie_release_web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);
    User findByTelegramId(String telegramId);
    boolean existsByLogin(String login);
    boolean existsByIdNotAndLogin(Long id, String login);
}
