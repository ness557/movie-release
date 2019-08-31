package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.repository.UserRepository;
import com.ness.movie_release_web.service.email.EmailService;
import com.ness.movie_release_web.service.telegram.TelegramNotificationBot;
import com.ness.movie_release_web.service.telegram.TelegramService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * PasswordRestoreServiceImpl.
 *
 * @author Roman_Bezruchko
 */

@Service
@RequiredArgsConstructor
public class PasswordRestoreServiceImpl implements PasswordRestoreService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final TelegramNotificationBot telegramBot;
    private final EmailService emailService;

    @Value("${telegram.webInterfaceLink}")
    private String appLink;

    private Map<String, User> passwordRestoreTokenMap = new WeakHashMap<>();

    @Override
    public List<String> updatePassword(String login, String oldPassword, String newPassword, Locale locale) {

        User user = userRepository.findByLogin(login);

        if (!passwordEncoder.matches(oldPassword, user.getEncPassword())) {
            return Collections.singletonList(messageSource.getMessage("lang.wrong_password_error", new Object[]{}, locale));
        }

        user.setEncPassword(newPassword);
        user.setEncPassword(passwordEncoder.encode(user.getEncPassword()));;

        return Collections.emptyList();
    }

    @Override
    public String createRestoreToken(User user) {
        String token = DigestUtils.md5Hex(user.getLogin());
        passwordRestoreTokenMap.put(token, user);

        return token;
    }

    @Override
    public void sendRestoreViaTelegram(String resetToken, Long telegramChatId) {

        String text = "[Click here](http://" + appLink +"/recoverPassword/" + resetToken + ") to restore password.";
        telegramBot.sendNotify(text, telegramChatId);
    }

    @Override
    public void sendRestoreViaEmail(String resetToken, String email) {

        String resetLink = "http://" + appLink +"/recoverPassword/" + resetToken;
        emailService.sendResetLink(resetLink, email);
    }

    @Override
    public Optional<User> getByPasswordToken(String token) {
        return Optional.ofNullable(passwordRestoreTokenMap.remove(token));
    }

    @Override
    public boolean tokenExists(String token) {
        return passwordRestoreTokenMap.containsKey(token);
    }
}
