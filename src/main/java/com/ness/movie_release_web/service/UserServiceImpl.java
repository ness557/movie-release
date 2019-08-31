package com.ness.movie_release_web.service;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.PasswordChangeDto;
import com.ness.movie_release_web.dto.PasswordResetResponseDto;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static com.ness.movie_release_web.model.type.MessageDestinationType.EMAIL;
import static com.ness.movie_release_web.model.type.MessageDestinationType.TELEGRAM;

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

        switch (user.getMessageDestinationType()) {
            case EMAIL:
                passwordRestoreService.sendRestoreViaEmail(resetToken, user.getEmail());
                resultDto.setSource(EMAIL)
                        .setAddress(user.getEmail());
                break;
            case TELEGRAM:
                passwordRestoreService.sendRestoreViaTelegram(resetToken, user.getTelegramChatId());
                resultDto.setSource(TELEGRAM)
                        .setAddress(user.getTelegramId());
                break;
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

    public User findByLogin(String login) {
        return repository.findByLogin(login);
    }
}
