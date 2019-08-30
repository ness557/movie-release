package com.ness.movie_release_web.dto.tmdb;

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
public class TmdbVideosDto {

    @JsonProperty("results")
    private List<TmdbVideoDto> videoList = new ArrayList<>();
}
