package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;

import java.util.Optional;

public interface UserService {

    void save(User user);
    void saveWithPassEncryption(User user);
    User get(Long id);
    Iterable<User> getAll();
    User findByLogin(String login);
    boolean isExists(String login);
    boolean existsByIdNotAndLogin(Long id, String login);
    User findByTelegramId(String telegramId);
    Optional<User> findByTelegramIdOrEmail(String telegramId, String email);
}
