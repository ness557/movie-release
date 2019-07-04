package com.ness.movie_release_web.model.dto.tmdb;

import java.util.Locale;

public enum Language {
    en("en", Locale.ENGLISH),
    ru("ru", new Locale.Builder().setLanguage("ru").setScript("Cyrl").build());

    private String value;
    private Locale locale;

    Language(String value, Locale locale) {
        this.value = value;
        this.locale = locale;
    }

    public String getValue() {
        return value;
    }

    public Locale getLocale() {
        return locale;
    }
}
