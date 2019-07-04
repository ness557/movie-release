package com.ness.movie_release_web.controller;

import java.security.Principal;

import com.ness.movie_release_web.model.dto.tmdb.Mode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${telegram.bot}")
    private String telegramBot;

    @GetMapping("/")
    private String index(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    private String home(@CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                        Principal principal){

        if (principal != null) {
            return "redirect:/" + mode + "/subscriptions";
        }

        return "redirect:/login";
    }
}
