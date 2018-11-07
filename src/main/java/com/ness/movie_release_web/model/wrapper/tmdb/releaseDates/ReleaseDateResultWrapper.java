package com.ness.movie_release_web.model.wrapper.tmdb.releaseDates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReleaseDateResultWrapper {

    @JsonProperty("release_dates")
    private List<ReleaseDateWrapper> releaseDateWrappers = new ArrayList<>();
}