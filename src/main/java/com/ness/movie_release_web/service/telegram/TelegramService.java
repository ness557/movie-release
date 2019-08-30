package com.ness.movie_release_web.service.telegram;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;

public interface TelegramService {
    void sendMovieNotify(User user, TmdbMovieDetailsDto movie, TmdbReleaseDate releaseDate);
    void sendEpisodeNotify(User user, TmdbEpisodeDto episode, TmdbTVDetailsDto show);
    void sendSeasonNotify(User user, TmdbSeasonDto season, TmdbTVDetailsDto show);
}
