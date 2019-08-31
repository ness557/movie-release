package com.ness.movie_release_web.service;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.movie.MovieDiscoverDto;
import com.ness.movie_release_web.dto.movie.MovieDto;
import com.ness.movie_release_web.dto.movie.MovieSearchDto;
import com.ness.movie_release_web.dto.movie.MovieSubscriptionsDto;
import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.dto.tmdb.movie.details.Status;
import com.ness.movie_release_web.repository.MovieSortBy;

import java.util.List;

public interface MovieService {

    MovieDto getMovie(Long tmdbMovieId, String login, Language language);

    MovieSearchDto search(String query, Long page, Long year, Language language, String login);

    MovieSubscriptionsDto getSubscriptions(List<Status> statuses,
                                           MovieSortBy sort,
                                           Boolean viewMode,
                                           Long page,
                                           String login,
                                           Language language);

    MovieDiscoverDto discover(TmdbDiscoverSearchCriteria criteria, String login);

    void updateDb();
}
