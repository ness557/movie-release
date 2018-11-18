package com.ness.movie_release_web.model.wrapper.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Videos {

    @JsonProperty("results")
    private List<Video> videoList = new ArrayList<>();
}
