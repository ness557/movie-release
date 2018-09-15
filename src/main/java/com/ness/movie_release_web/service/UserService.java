package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;

public interface UserService {

    void save(User user);
    User get(Long id);
    void delete(User user);
    void delete(Long id);
    Iterable<User> getAll();
    User findByLogin(String login);
    boolean isExists(String login);
}
