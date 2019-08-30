package com.ness.movie_release_web.dto.tmdb.tvSeries.details;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    Returning_Series,
    Planned,
    In_Production,
    Ended,
    Canceled,
    Pilot;

    @JsonCreator
    public static Status forValue(String value){
        String replaced = value.replace(" ", "_");
        return Status.valueOf(replaced);
    }
}
