package com.ness.movie_release_web.dto.movie;

import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MovieDto {
    private TmdbMovieDetailsDto movieDetailsDto;
    private Boolean subscribed;
    private List<TmdbReleaseDate> releases;
}
