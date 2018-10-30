package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Locale;

@Service
public class DBLocaleResolver implements LocaleResolver {

    private UserService userService;

    @Autowired
    public DBLocaleResolver(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {

        Principal principal = httpServletRequest.getUserPrincipal();

        if(principal != null){
            User user = userService.findByLogin(principal.getName());
            return user.getLanguage().getLocale();
        }

        return Locale.ENGLISH;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}
