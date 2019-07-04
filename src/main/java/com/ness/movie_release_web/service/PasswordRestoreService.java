package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * PasswordRestoreService.
 *
 * @author Roman_Bezruchko
 */
public interface PasswordRestoreService {

    List<String> changePassword(User user, String oldPassword, String newPassword, Locale locale);
    Optional<User> getByPasswordToken(String token);
    boolean tokenExists(String token);
    String createRestoreToken(User user);
    void sendRestoreViaTelegram(String resetToken, Long telegramChatId);
    void sendRestoreViaEmail(String resetToken, String email);
}
