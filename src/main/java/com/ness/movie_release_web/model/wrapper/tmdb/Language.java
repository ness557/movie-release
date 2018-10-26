package com.ness.movie_release_web.model.wrapper.tmdb;

public enum Language {
    en("en", "English"),
    ru("ru", "Russian");

    private String value;
    private String string;

    Language(String value, String string) {
        this.value = value;
        this.string = string;
    }

    public String getValue() {
        return value;
    }

    public String getString() {
        return string;
    }
}
