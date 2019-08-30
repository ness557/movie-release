package com.ness.movie_release_web.dto.tmdb.credits;

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
public class TmdbMovieCreditsDto {

    @JsonProperty("cast")
    private List<TmdbMovieCastDto> casts = new ArrayList<>();
    @JsonProperty("id")
    private Long id;
    @JsonProperty("crew")
    private List<TmdbMovieCrewDto> crews = new ArrayList<>();
}
