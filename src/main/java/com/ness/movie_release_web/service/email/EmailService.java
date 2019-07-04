package com.ness.movie_release_web.service.email;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.MovieDetailsDto;
import com.ness.movie_release_web.model.dto.tmdb.releaseDates.ReleaseDate;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.EpisodeDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.SeasonDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.TVDetailsDto;

public interface EmailService {
    void sendMovieNotify(User user, MovieDetailsDto movie, ReleaseDate releaseDate);
    void sendEpisodeNotify(User user, EpisodeDto episode, TVDetailsDto show);
    void sendSeasonNotify(User user, SeasonDto season, TVDetailsDto show);
    void sendResetLink(String resetLink, String email);
}
