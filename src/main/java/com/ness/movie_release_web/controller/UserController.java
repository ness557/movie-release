package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.Mode;
import com.ness.movie_release_web.service.RegistrationService;
import com.ness.movie_release_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Locale;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

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

        model.addAttribute("mode", mode);
        model.addAttribute("language", language);

        boolean isAuthenticated = principal != null;

        List<String> errors =
                registrationService.validate(user, locale, recaptchaResponse, request.getRemoteAddr());

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "register";
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (isAuthenticated) {
            registrationService.editUser(user, principal.getName());
        } else {
            registrationService.registerUser(user);
        }

        response.addCookie(new Cookie("language", language.getValue()));

        if(isAuthenticated && !principal.getName().equals(user.getLogin())){
            request.logout();
        }

        if (!isAuthenticated) {
            request.login(user.getLogin(), user.getMatchPassword());
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
        model.addAttribute(user);
        return "register";
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
}
