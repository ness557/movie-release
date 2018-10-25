package com.ness.movie_release_web.model.wrapper.tmdb;

public enum Language {
    en("en"),
    ru("ru");

    private String value;

    Language(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
