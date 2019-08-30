package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void save(User user) {
        repository.save(user);
    }

    public void saveWithPassEncryption(User user) {
        user.setEncPassword(passwordEncoder.encode(user.getEncPassword()));
        repository.save(user);
    }

    @Override
    public User get(Long id) {
        return repository.getOne(id);
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

    @Override
    public boolean existsByIdNotAndLogin(Long id, String login) {
        return repository.existsByIdNotAndLogin(id, login);
    }

    @Override
    public User findByTelegramId(String telegramId) {
        return repository.findByTelegramId(telegramId);
    }

    @Override
    public Optional<User> findByTelegramIdOrEmail(String telegramId, String email) {
        return repository.findByTelegramIdOrEmail(telegramId, email);
    }
}
