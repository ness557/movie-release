package com.ness.movie_release_web.model.dto.tmdb.credits;

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
public class MovieCreditsDto {

    @JsonProperty("cast")
    private List<MovieCastDto> casts = new ArrayList<>();
    @JsonProperty("id")
    private Long id;
    @JsonProperty("crew")
    private List<MovieCrewDto> crews = new ArrayList<>();
}
