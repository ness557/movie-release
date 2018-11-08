package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.Mode;
import com.ness.movie_release_web.security.TokenService;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.google.recapcha.RecaptchaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@SessionAttributes(names = {"language"}, types = {Language.class})
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    @Qualifier("myUserDetailService")
    private UserDetailsService userDetailsService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RecaptchaService recaptchaService;

    @GetMapping("/login")
    public String login(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("language", service.findByLogin(principal.getName()).getLanguage());
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("language", service.findByLogin(principal.getName()).getLanguage());
        }

        model.addAttribute(new User());
        return "register";
    }

    @PostMapping("/login")
    public ResponseEntity postLogin(@RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    @RequestParam(name = "g-recaptcha-response") String recaptchaResponse,
                                    HttpServletResponse response,
                                    HttpServletRequest request) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String token = tokenService.getToken(userDetails);
        response.addCookie(new Cookie("authorization", "bearer:" + token));

        return ResponseEntity.ok("/home");
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user,
                           BindingResult bindingResult,
                           @RequestParam(name = "g-recaptcha-response") String recaptchaResponse,
                           Model model,
                           Locale locale,
                           HttpServletResponse response,
                           HttpServletRequest request,
                           Principal principal) {

        if (principal != null) {
            User fromDB = service.findByLogin(principal.getName());
            model.addAttribute("language", fromDB.getLanguage());
            model.addAttribute("mode", fromDB.getMode());
            user.setTelegramId(fromDB.getTelegramId());
        }

        List<String> errors = new ArrayList<>();

        if (!user.getEncPassword().equals(user.getMatchPassword()))
            errors.add(messageSource.getMessage("lang.passwords_not_match", new Object[]{}, locale));

        if (user.getEmail().isEmpty() && !user.isTelegramNotify())
            errors.add(messageSource.getMessage("lang.empty_email", new Object[]{}, locale));

        if (user.getTelegramId().isEmpty() && user.isTelegramNotify())
            errors.add(messageSource.getMessage("lang.empty_telegram_id", new Object[]{}, locale));

        if (user.getId() == null) {
            if (service.isExists(user.getLogin()))
                errors.add(messageSource.getMessage("lang.user_exists", new Object[]{}, locale));
        } else {
            if (service.existsByIdNotAndLogin(user.getId(), user.getLogin()))
                errors.add(messageSource.getMessage("lang.login_used", new Object[]{}, locale));
        }

        if (!recaptchaService.verifyRecaptcha(request.getRemoteAddr(), recaptchaResponse))
            errors.add(messageSource.getMessage("lang.recaptcha_error", new Object[]{}, locale));

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "register";
        }

        if (bindingResult.hasErrors())
            return "register";

        user.setRole("ROLE_USER");
        user.setTelegramId(StringUtils.lowerCase(user.getTelegramId()));
        user.setMode(Mode.movie);

        service.saveWithPassEncryption(user);

        postLogin(user.getLogin(), user.getMatchPassword(), recaptchaResponse, response, request);

        return "redirect:/home";
    }

    @GetMapping("/userInfo")
    public String userInfo(Model model, Principal principal) {

        User user = service.findByLogin(principal.getName());
        model.addAttribute("language", user.getLanguage());
        model.addAttribute("mode", user.getMode());
        model.addAttribute(user);
        return "register";
}

    @PostMapping("/setLanguage")
    public ResponseEntity setLanguage(@RequestParam(value = "language") Language language, Principal principal, Model model) {
        User user = service.findByLogin(principal.getName());
        user.setLanguage(language);
        service.save(user);

        model.addAttribute("language", language);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/setMode")
    public ResponseEntity setMode(@RequestParam(value = "mode") Mode mode, Principal principal, Model model){
        User user = service.findByLogin(principal.getName());
        user.setMode(mode);
        service.save(user);

        model.addAttribute("mode", mode);

        return ResponseEntity.ok().build();
    }
}
