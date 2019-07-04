package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.dto.PasswordDto;
import com.ness.movie_release_web.model.dto.UserDto;
import com.ness.movie_release_web.model.dto.UserMapper;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.Mode;
import com.ness.movie_release_web.model.dto.tmdb.PasswordChangeDto;
import com.ness.movie_release_web.service.PasswordRestoreService;
import com.ness.movie_release_web.service.RegistrationService;
import com.ness.movie_release_web.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private RegistrationService registrationService;
    private MessageSource messageSource;
    private PasswordRestoreService passwordRestoreService;

    @GetMapping("/login")
    public String login(@CookieValue(value = "language", defaultValue = "en") Language language,
                        @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                        Model model) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(@CookieValue(value = "language", defaultValue = "en") Language language,
                               @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                               Model model) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        model.addAttribute("user", new UserDto().setPassword(new PasswordDto()));
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute(name = "user") UserDto userDto,
                           BindingResult bindingResult,
                           @RequestParam(name = "g-recaptcha-response", required = false) String recaptchaResponse,
                           @CookieValue(value = "language", defaultValue = "en") Language language,
                           @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                           Model model, Locale locale,
                           HttpServletResponse response,
                           HttpServletRequest request,
                           Principal principal) throws ServletException {

        model.addAttribute("mode", mode);
        model.addAttribute("language", language);

        if (bindingResult.hasErrors()) {
            return "register";
        }

        User user = UserMapper.mapToUser(userDto);
        boolean isAuthenticated = principal != null;

        List<String> errors = isAuthenticated
                ? registrationService.validateEdit(user, locale)
                : registrationService.validateRegistration(user, locale, recaptchaResponse, request.getRemoteAddr());

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "register";
        }

        if (isAuthenticated) {
            registrationService.editUserInfo(user);
        } else {
            registrationService.registerUser(user);
        }

        response.addCookie(new Cookie("language", language.getValue()));

        if (isAuthenticated && !principal.getName().equals(user.getLogin())) {
            request.logout();
        }

        if (!isAuthenticated) {
            request.login(userDto.getLogin(), userDto.getPassword().getPassword());
        }

        return "redirect:/home";
    }

    @GetMapping("/userInfo")
    public String userInfo(@CookieValue(value = "language", defaultValue = "en") Language language,
                           @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                           Model model,
                           Principal principal) {

        User user = userService.findByLogin(principal.getName());
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute("user", UserMapper.mapToDto(user).setPassword(null));
        return "register";
    }

    @GetMapping("/changePassword")
    public String changePassword(@CookieValue(value = "language", defaultValue = "en") Language language,
                                 @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                                 Model model) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute("passwordChange", new PasswordChangeDto().setPassword(new PasswordDto()));

        return "changePassword";
    }

    @GetMapping("/resetPassword")
    public String resetPassword(@CookieValue(value = "language", defaultValue = "en") Language language,
                                @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                                Model model) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        return "resetPassword";
    }

    @PostMapping("/setLanguage")
    @ResponseStatus(value = HttpStatus.OK)
    public void setLanguage(@RequestParam(value = "language") Language language,
                            Principal principal,
                            HttpServletResponse response) {

        User user = userService.findByLogin(principal.getName());
        user.setLanguage(language);
        userService.save(user);

        response.addCookie(new Cookie("language", language.getValue()));
    }

    @PostMapping("/setMode")
    @ResponseStatus(value = HttpStatus.OK)
    public void setMode(@RequestParam(value = "mode") Mode mode,
                        HttpServletResponse response) {
        response.addCookie(new Cookie("mode", mode.name()));
    }

    @PostMapping("/changePassword")
    public String changePass(@Valid @ModelAttribute("passwordChange") PasswordChangeDto dto,
                             BindingResult bindingResult,
                             @CookieValue(value = "language", defaultValue = "en") Language language,
                             @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                             Locale locale,
                             Model model,
                             Principal principal) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        if (bindingResult.hasErrors()) {
            return "changePassword";
        }

        User user = userService.findByLogin(principal.getName());
        String newPassword = dto.getPassword().getPassword();

        List<String> errors =
                passwordRestoreService.changePassword(user, dto.getOldPassword(), newPassword, locale);

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "changePassword";
        }

        return "passwordChanged";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam(name = "emailOrTelegram") String emailOrTg,
                                Locale locale,
                                Model model) {
        Optional<User> userOpt = userService.findByTelegramIdOrEmail(emailOrTg, emailOrTg);

        if (!userOpt.isPresent()) {
            model.addAttribute("emailOrTelegram", emailOrTg);
            model.addAttribute("errorMessage", messageSource.getMessage("lang.user_not_found", new Object[]{}, locale));
            return "resetPassword";
        }

        User user = userOpt.get();
        String resetToken = passwordRestoreService.createRestoreToken(user);

        if (user.isTelegramNotify()) {
            model.addAttribute("receiverService", "Telegram");
            model.addAttribute("receiverAddress", user.getTelegramId());
            passwordRestoreService.sendRestoreViaTelegram(resetToken, user.getTelegramChatId());
        } else {
            model.addAttribute("receiverService", "Email");
            model.addAttribute("receiverAddress", user.getEmail());
            passwordRestoreService.sendRestoreViaEmail(resetToken, user.getEmail());
        }

        return "resetLinkSent";
    }

    @GetMapping("/recoverPassword/{token}")
    public String recoverPassword(@PathVariable("token") String token,
                                  @CookieValue(value = "language", defaultValue = "en") Language language,
                                  @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                                  Model model) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        if (!passwordRestoreService.tokenExists(token)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        model.addAttribute("passwordDto", new PasswordChangeDto().setPassword(new PasswordDto()));
        model.addAttribute("token", token);

        return "recoverPassword";
    }

    @PostMapping("/recoverPassword/{token}")
    public String restorePassword(@PathVariable("token") String token,
                                  @Valid @ModelAttribute("passwordDto") PasswordChangeDto dto,
                                  BindingResult bindingResult,
                                  @CookieValue(value = "language", defaultValue = "en") Language language,
                                  @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                                  Model model,
                                  HttpServletRequest request) throws ServletException {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        if (bindingResult.hasErrors()) {
            return "recoverPassword";
        }

        Optional<User> userOpt = passwordRestoreService.getByPasswordToken(token);

        if (!userOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();

        userService.saveWithPassEncryption(user.setEncPassword(dto.getPassword().getPassword()));

        request.login(user.getLogin(), dto.getPassword().getPassword());

        return "passwordChanged";
    }
}
