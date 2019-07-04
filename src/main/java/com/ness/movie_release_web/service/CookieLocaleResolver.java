package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.dto.tmdb.Language;

import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class CookieLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {

        if (request.getCookies() != null) {
            Optional<Cookie> cookieOpt = Arrays.stream(request.getCookies()).filter(c -> "language".equals(c.getName()))
                    .findFirst();
            if (cookieOpt.isPresent()) {
                Cookie lang = cookieOpt.get();
                return Language.valueOf(lang.getValue()).getLocale();
            }
        }

        return Locale.ENGLISH;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Locale locale) {

    }
}
