package com.ness.movie_release_web.service.telegram;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDate;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.EpisodeWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.SeasonWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;

public interface TelegramService {
    void sendMovieNotify(User user, MovieDetailsWrapper movie, ReleaseDate releaseDate);
    void sendEpisodeNotify(User user, EpisodeWrapper episode, TVDetailsWrapper show);
    void sendSeasonNotify(User user, SeasonWrapper season, TVDetailsWrapper show);
}
