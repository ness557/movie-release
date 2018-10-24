package com.ness.movie_release_web.model.wrapper.tmdbReleaseDates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReleaseDateResult {

    @JsonProperty("iso_3166_1")
    private String iso31661;
    @JsonProperty("release_dates")
    private List<ReleaseDate> releaseDates = new ArrayList<>();
}
