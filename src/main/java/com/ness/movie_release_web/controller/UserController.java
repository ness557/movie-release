package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.Mode;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.google.recapcha.RecaptchaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RecaptchaService recaptchaService;

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
                                Model model,
                                Principal principal) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        model.addAttribute(new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user, BindingResult bindingResult,
                            @RequestParam(name = "g-recaptcha-response") String recaptchaResponse, 
                            @CookieValue(value = "language", defaultValue = "en") Language language,
                            @CookieValue(value = "mode", defaultValue = "movie") Mode mode, 
                            Model model, Locale locale,
                            HttpServletResponse response, 
                            HttpServletRequest request, 
                            Principal principal) throws ServletException {

        User fromDB = null;
        if (principal != null) {
            fromDB = service.findByLogin(principal.getName());
            user.setTelegramId(fromDB.getTelegramId());
        }
        model.addAttribute("mode", mode);
        model.addAttribute("language", language);

        List<String> errors = new ArrayList<>();

        if (!user.getEncPassword().equals(user.getMatchPassword()))
            errors.add(messageSource.getMessage("lang.passwords_not_match", new Object[] {}, locale));

        if (user.getEmail().isEmpty() && !user.isTelegramNotify())
            errors.add(messageSource.getMessage("lang.empty_email", new Object[] {}, locale));

        if (user.getTelegramId().isEmpty() && user.isTelegramNotify())
            errors.add(messageSource.getMessage("lang.empty_telegram_id", new Object[] {}, locale));

        if (user.getId() == null) {
            if (service.isExists(user.getLogin()))
                errors.add(messageSource.getMessage("lang.user_exists", new Object[] {}, locale));
        } else {
            if (service.existsByIdNotAndLogin(user.getId(), user.getLogin()))
                errors.add(messageSource.getMessage("lang.login_used", new Object[] {}, locale));
        }

        if (!recaptchaService.verifyRecaptcha(request.getRemoteAddr(), recaptchaResponse))
            errors.add(messageSource.getMessage("lang.recaptcha_error", new Object[] {}, locale));

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "register";
        }

        if (bindingResult.hasErrors())
            return "register";

        user.setRole(fromDB != null && !fromDB.getRole().isEmpty() ? fromDB.getRole() : "ROLE_USER");
        user.setTelegramId(StringUtils.lowerCase(user.getTelegramId()));

        service.saveWithPassEncryption(user);

        response.addCookie(new Cookie("language", language.getValue()));

//        login(user.getLogin(), user.getMatchPassword());
        request.login(user.getLogin(), user.getMatchPassword());
        return "redirect:/home";
    }

    @GetMapping("/userInfo")
    public String userInfo(@CookieValue(value = "language", defaultValue = "en") Language language,
                            @CookieValue(value = "mode", defaultValue = "movie") Mode mode, 
                            Model model,
            Principal principal) {

        User user = service.findByLogin(principal.getName());
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute(user);
        return "register";
    }

    @PostMapping("/setLanguage")
    @ResponseStatus(value = HttpStatus.OK)
    public void setLanguage(@RequestParam(value = "language") Language language,
                                        Principal principal,
                                        HttpServletResponse response) {

        User user = service.findByLogin(principal.getName());
        user.setLanguage(language);
        service.save(user);

        response.addCookie(new Cookie("language", language.getValue()));
    }

    @PostMapping("/setMode")
    @ResponseStatus(value = HttpStatus.OK)
    public void setMode(@RequestParam(value = "mode") Mode mode, 
                                    HttpServletResponse response) {
        response.addCookie(new Cookie("mode", mode.name()));
    }
}
