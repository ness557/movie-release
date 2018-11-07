package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Value("${telegram.bot}")
    private String telegramBot;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    private String index(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    private String home(Model model, Principal principal){

        if (principal != null) {
            User user = userService.findByLogin(principal.getName());
            return "redirect:/" + user.getMode() + "/subscriptions";
        }

        model.addAttribute("telegram", telegramBot);
        return "home";
    }

}
