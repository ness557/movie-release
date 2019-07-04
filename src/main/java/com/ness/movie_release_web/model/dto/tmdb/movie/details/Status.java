package com.ness.movie_release_web.model.dto.tmdb.movie.details;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    Empty,
    Rumored,
    Planned,
    In_Production,
    Post_Production,
    Released,
    Canceled;

    @JsonCreator
    public static Status forValue(String value) {
        if(value == null)
            return Status.Empty;

        String replaced = value.replace(" ", "_");
        return Status.valueOf(replaced);
    }
}
