package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface RegistrationService {

    List<String> validateRegistration(User user, Locale locale, String recaptchaResponse, String ip);
    List<String> validateEdit(User user, Locale locale);
    void registerUser(User user);
    void editUserInfo(User user);
}
