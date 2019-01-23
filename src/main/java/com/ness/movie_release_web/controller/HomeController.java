package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.Mode;
import com.ness.movie_release_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
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
    private String home(@CookieValue(value = "mode", required = false) Mode mode){

        if (mode != null) {
            return "redirect:/" + mode + "/subscriptions";
        }

        return "redirect:/login";
    }
}
