package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SecurityController {

    @Autowired
    private UserService service;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {

        model.addAttribute(new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user,
                           BindingResult bindingResult, Model model, HttpServletRequest request) {

        List<String> errors = new ArrayList<>();

        if(!user.getEncPassword().equals(user.getMatchPassword()))
            errors.add("Passwords does not match");

        if(user.getEmail().isEmpty() && !user.isTelegramNotify())
            errors.add("Email address is empty");

        if(user.getTelegramId().isEmpty() && user.isTelegramNotify())
            errors.add("Telegram id is empty");

        if (user.getId() == null)
            if(service.isExists(user.getLogin()))
                errors.add("Such user already exists");


        if(!errors.isEmpty()){
            model.addAttribute("errors", errors);
            return "register";
        }

        if(bindingResult.hasErrors())
            return "register";

        user.setRole("ROLE_USER");
        user.setTelegramId(StringUtils.lowerCase(user.getTelegramId()));
        service.save(user);

        return "redirect:/user/subscriptions";
    }

    @GetMapping("/userInfo")
    public String userInfo(Model model, Principal principal){

        model.addAttribute(service.findByLogin(principal.getName()));
        return "register";
    }
}
