package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.security.TokenService;
import com.ness.movie_release_web.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SecurityController {

    @Autowired
    private UserService service;

    @Autowired
    @Qualifier("myUserDetailService")
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {

        model.addAttribute(new User());
        return "register";
    }

    @PostMapping("/login")
    public ResponseEntity postLogin(@RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    HttpServletResponse response) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String token = tokenService.getToken(userDetails);
        response.addCookie(new Cookie("authorization", "bearer:" + token));

        return ResponseEntity.ok("/user/subscriptions");
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user,
                           BindingResult bindingResult,
                           Model model,
                           HttpServletResponse response) {

        List<String> errors = new ArrayList<>();

        if (!user.getEncPassword().equals(user.getMatchPassword()))
            errors.add("Passwords does not match");

        if (user.getEmail().isEmpty() && !user.isTelegramNotify())
            errors.add("Email address is empty");

        if (user.getTelegramId().isEmpty() && user.isTelegramNotify())
            errors.add("Telegram id is empty");

        if (user.getId() == null)
            if (service.isExists(user.getLogin()))
                errors.add("Such user already exists");

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "register";
        }

        if (bindingResult.hasErrors())
            return "register";

        user.setRole("ROLE_USER");
        user.setTelegramId(StringUtils.lowerCase(user.getTelegramId()));
        service.saveWithPassEncryption(user);

        postLogin(user.getLogin(), user.getMatchPassword(), response);

        return "redirect:/user/subscriptions";
    }

    @GetMapping("/userInfo")
    public String userInfo(Model model, Principal principal) {

        User user = service.findByLogin(principal.getName());
        model.addAttribute("language", user.getLanguage());
        model.addAttribute(user);
        return "register";
    }
}
