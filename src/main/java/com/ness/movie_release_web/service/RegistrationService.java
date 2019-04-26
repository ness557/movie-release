package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;

import java.util.List;
import java.util.Locale;

public interface RegistrationService {

    List<String> validate(User user, Locale locale, String recaptchaResponse, String ip);
    void registerUser(User user);
    void editUser(User user, String login);
}
