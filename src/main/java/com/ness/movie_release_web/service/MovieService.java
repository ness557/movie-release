package com.ness.movie_release_web.service;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.MovieDiscoverDto;
import com.ness.movie_release_web.dto.MovieDto;
import com.ness.movie_release_web.dto.MovieSearchDto;
import com.ness.movie_release_web.dto.MovieSubscriptionsDto;
import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.dto.tmdb.movie.details.Status;
import com.ness.movie_release_web.repository.MovieSortBy;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

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
}
