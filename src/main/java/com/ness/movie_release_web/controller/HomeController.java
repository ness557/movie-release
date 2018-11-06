package com.ness.movie_release_web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

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
            return "redirect:/movie/subscriptions";
        }

        model.addAttribute("telegram", telegramBot);
        return "home";
    }

    @PostMapping("/setMode")
    public ResponseEntity setMode(HttpServletRequest request, HttpServletResponse response){

        Optional<Cookie> first = Arrays.stream(request.getCookies()).filter(e -> "mode".equals(e.getName())).findFirst();

        Cookie cookie = first.orElse(new Cookie("mode", Mode.movie.name()));
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    enum Mode{
        series,
        movie
    }
}
