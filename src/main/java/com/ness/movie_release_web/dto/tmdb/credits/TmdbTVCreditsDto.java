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
public class TmdbTVCreditsDto {

    @JsonProperty("cast")
    private List<TmdbTVCastDto> casts = new ArrayList<>();
    @JsonProperty("crew")
    private List<TmdbTVCrewDto> crews = new ArrayList<>();
}
