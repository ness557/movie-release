package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.repository.UserRepository;
import com.ness.movie_release_web.service.google.recapcha.RecaptchaService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final RecaptchaService recaptchaService;

    /**
     * Validates user before registration.
     *
     * @param user              user model
     * @param locale            current user locale
     * @param recaptchaResponse google recaptcha response
     * @param ip                user remote ip
     * @return localized list of errors
     */
    @Override
    public List<String> validateRegistration(User user, Locale locale, String recaptchaResponse, String ip) {
        List<String> errors = new ArrayList<>();

        validateUser(user, locale, errors);

        if (!recaptchaService.verifyRecaptcha(ip, recaptchaResponse))
            errors.add(messageSource.getMessage("lang.recaptcha_error", new Object[]{}, locale));

        return errors;
    }

    /**
     * Validates user before editing account.
     *
     * @param user              user model
     * @param locale            current user locale
     * @return localized list of errors
     */
    @Override
    public List<String> validateEdit(User user, Locale locale) {
        List<String> errors = new ArrayList<>();
        validateUser(user, locale, errors);

        return errors;
    }

    @Override
    public void registerUser(User user) {
        user.setRole("ROLE_USER");
        user.setTelegramId(StringUtils.lowerCase(user.getTelegramId()));

        userService.saveWithPassEncryption(user);
    }

    @Override
    public void editUserInfo(User user) {
        User fromDB = userRepository.getOne(user.getId());

        user.setEncPassword(fromDB.getEncPassword());
        user.setTelegramId(StringUtils.lowerCase(user.getTelegramId()));
        userRepository.save(user);
    }

    private void validateUser(User user, Locale locale, List<String> errors) {

        if (user.getId() == null) {
            if (userRepository.existsByLogin(user.getLogin()))
                errors.add(messageSource.getMessage("lang.user_exists", new Object[]{}, locale));
        } else {
            if (userRepository.existsByIdNotAndLogin(user.getId(), user.getLogin()))
                errors.add(messageSource.getMessage("lang.login_used", new Object[]{}, locale));
        }
    }
}
