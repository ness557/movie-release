package com.ness.movie_release_web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {

    @Value("${telegram.bot}")
    private String telegramBot;

    @GetMapping("/")
    private String index(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    private String home(Model model, Principal principal){

        if (principal != null) {
            return "redirect:/user/subscriptions";
        }

        model.addAttribute("telegram", telegramBot);
        return "home";
    }
}
