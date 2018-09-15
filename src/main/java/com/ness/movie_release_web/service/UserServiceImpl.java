package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void save(User user) {
        user.setEncPassword(passwordEncoder.encode(user.getEncPassword()));
        repository.save(user);
    }

    @Override
    public User get(Long id) {
        return repository.getOne(id);
    }

    @Override
    public void delete(User user) {
        repository.delete(user);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Iterable<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User findByLogin(String login) {
        return repository.findByLogin(login);
    }

    @Override
    public boolean isExists(String login) {
        return repository.existsByLogin(login);
    }
}
