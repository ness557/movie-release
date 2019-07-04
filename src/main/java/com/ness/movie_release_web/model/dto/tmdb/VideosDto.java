package com.ness.movie_release_web.model.dto.tmdb;

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
public class VideosDto {

    @JsonProperty("results")
    private List<VideoDto> videoList = new ArrayList<>();
}
