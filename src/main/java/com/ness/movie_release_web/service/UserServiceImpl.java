package com.ness.movie_release_web.service;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.PasswordChangeDto;
import com.ness.movie_release_web.dto.PasswordResetResponseDto;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.type.NotificationSource;
import com.ness.movie_release_web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.ness.movie_release_web.model.type.NotificationSource.EMAIL;
import static com.ness.movie_release_web.model.type.NotificationSource.TELEGRAM;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordRestoreService passwordRestoreService;

    @Override
    public void saveWithPassEncryption(User user) {
        user.setEncPassword(passwordEncoder.encode(user.getEncPassword()));
        repository.save(user);
    }

    @Override
    public PasswordResetResponseDto sendResetPasswordResponse(String emailOrTg) throws UserNotFoundException {
        User user = repository.findByTelegramIdOrEmail(emailOrTg, emailOrTg).orElseThrow(UserNotFoundException::new);
        String resetToken = passwordRestoreService.createRestoreToken(user);

        PasswordResetResponseDto resultDto = new PasswordResetResponseDto();

        if (user.isTelegramNotify()) {
            passwordRestoreService.sendRestoreViaTelegram(resetToken, user.getTelegramChatId());
            resultDto.setSource(TELEGRAM)
                    .setAddress(user.getTelegramId());
        } else {
            passwordRestoreService.sendRestoreViaEmail(resetToken, user.getEmail());
            resultDto.setSource(EMAIL)
                    .setAddress(user.getEmail());
        }

        return resultDto;
    }

    @Override
    public User recoverPassword(String token, PasswordChangeDto dto) {
        User user = passwordRestoreService.getByPasswordToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        saveWithPassEncryption(user.setEncPassword(dto.getNewPassword()));

        return user;
    }

    @Override
    public void updateLanguage(String login, Language language) {
        repository.findByLogin(login).setLanguage(language);
    }

    @Override
    public User findByLogin(String login) {
        return repository.findByLogin(login);
    }
}
