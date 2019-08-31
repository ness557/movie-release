package com.ness.movie_release_web.service;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.PasswordChangeDto;
import com.ness.movie_release_web.dto.PasswordResetResponseDto;
import com.ness.movie_release_web.model.User;

import java.util.Optional;

public interface UserService {


    void updateLanguage(String login, Language language);

    void saveWithPassEncryption(User user);
    User findByLogin(String login);
    PasswordResetResponseDto sendResetPasswordResponse(String emailOrTg) throws UserNotFoundException;
    User recoverPassword(String token, PasswordChangeDto dto);
}
