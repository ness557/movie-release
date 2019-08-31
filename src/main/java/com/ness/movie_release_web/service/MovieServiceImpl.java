package com.ness.movie_release_web.service;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.movie.MovieDiscoverDto;
import com.ness.movie_release_web.dto.movie.MovieDto;
import com.ness.movie_release_web.dto.movie.MovieSearchDto;
import com.ness.movie_release_web.dto.movie.MovieSubscriptionsDto;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieDto;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieSearchDto;
import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.dto.tmdb.movie.details.Status;
import com.ness.movie_release_web.model.type.MessageDestinationType;
import com.ness.movie_release_web.repository.FilmRepository;
import com.ness.movie_release_web.repository.MovieSortBy;
import com.ness.movie_release_web.service.tmdb.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ness.movie_release_web.model.type.MessageDestinationType.*;
import static com.ness.movie_release_web.repository.FilmSpecifications.byUserAndStatusWithOrderby;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final FilmRepository repository;
    private final TmdbMovieService tmdbMovieService;
    private final UserService userService;
    private final TmdbDatesService tmdbDatesService;
    private final TmdbDiscoverService tmdbDiscoverService;
    private final TmdbCompanyService tmdbCompanyService;
    private final TmdbPeopleService tmdbPeopleService;
    private final TmdbGenreService tmdbGenreService;

    public MovieDto getMovie(Long tmdbMovieId, String login, Language language) {
        TmdbMovieDetailsDto movieDetails = tmdbMovieService.getMovieDetails(tmdbMovieId, language)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return new MovieDto()
                .setSubscribed(repository.existsByIdAndUsers(tmdbMovieId, userService.findByLogin(login)))
                .setMovieDetailsDto(movieDetails)
                .setReleases(tmdbDatesService.getReleaseDates(tmdbMovieId));
    }

    public MovieSearchDto search(String query, Long page, Long year, Language language, String login) {
        User user = userService.findByLogin(login);

        TmdbMovieSearchDto movieSearchDto =
                tmdbMovieService.searchForMovies(query, page, year, language)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));

        return new MovieSearchDto()
                .setTotal(movieSearchDto.getTotalPages())
                .setMovieSearchDtoMap(toDtoMap(movieSearchDto, user));
    }

    public MovieSubscriptionsDto getSubscriptions(List<Status> statuses,
                                                  MovieSortBy sort,
                                                  Boolean viewMode,
                                                  Long page,
                                                  String login,
                                                  Language language) {

        if (sort == null) {
            switch (language) {
                case en:
                    sort = MovieSortBy.NameEn_asc;
                    break;
                case ru:
                    sort = MovieSortBy.NameRu_asc;
                    break;
            }
        }

        int size = viewMode ? 30 : 10;

        User user = userService.findByLogin(login);

        Page<Film> filmPage = getByUserAndStatusWithOrderbyAndPages(statuses, sort, user, page.intValue() - 1, size);
        List<Film> films = filmPage.getContent();

        List<TmdbMovieDetailsDto> movieDtos;
        if (viewMode) {
            movieDtos = films.stream().map(f -> TmdbMovieDetailsDto.of(f, language)).collect(toList());
        } else {
            movieDtos = films.stream().map(f -> tmdbMovieService.getMovieDetails(f.getId(), language))
                    .filter(Optional::isPresent).map(Optional::get).collect(toList());
        }

        return new MovieSubscriptionsDto()
                .setMovieDetailsDtos(movieDtos)
                .setSortBy(sort)
                .setTotalPages((long) filmPage.getTotalPages())
                .setBotInitialized(user.getMessageDestinationType().equals(EMAIL) || user.getTelegramChatId() != null);
    }

    public MovieDiscoverDto discover(TmdbDiscoverSearchCriteria criteria, String login) {
        MovieDiscoverDto result = new MovieDiscoverDto();

        Language language = criteria.getLanguage();
        User user = userService.findByLogin(login);

        tmdbDiscoverService.discoverMovie(criteria)
                .ifPresent(dto ->
                        result.setTmdbMovieDtoMap(toDtoMap(dto, user))
                                .setTotalPages(dto.getTotalPages() > 1000
                                        ? 1000
                                        : dto.getTotalPages()));

        return result.setCompanies(tmdbCompanyService.getCompanies(criteria.getCompanies(), language))
                .setPeople(tmdbPeopleService.getPeopleList(criteria.getPeople(), language))
                .setGenres(tmdbGenreService.getMovieGenres(language));

    }

    @Override
    @Scheduled(cron = "${cron.pattern.updateDB}")
    public void updateDb() {

        log.info("Updating film db...");
        repository.findAll().forEach(f -> {
            Long tmdbId = f.getId();
            Optional<TmdbMovieDetailsDto> movieDetails = tmdbMovieService.getMovieDetails(tmdbId, Language.en);

            if (!movieDetails.isPresent()) {
                return;
            }

            TmdbMovieDetailsDto movieDetailsDto = movieDetails.get();
            f.setReleaseDate(movieDetailsDto.getReleaseDate());
            f.setVoteAverage(movieDetailsDto.getVoteAverage().floatValue());
            f.setNameEn(movieDetailsDto.getTitle());
            f.setStatus(movieDetailsDto.getStatus());

            Optional<TmdbMovieDetailsDto> movieDetailsRuOpt = tmdbMovieService.getMovieDetails(tmdbId, Language.ru);
            movieDetailsRuOpt.ifPresent(movieDetailsRu -> f.setNameRu(movieDetailsRu.getTitle()));

            repository.save(f);
        });
        log.info("film db updated!");
    }

    private Page<Film> getByUserAndStatusWithOrderbyAndPages(List<Status> statuses,
                                                            MovieSortBy sortBy,
                                                            User user,
                                                            Integer page,
                                                            Integer size) {
        return repository.findAll(byUserAndStatusWithOrderby(statuses, sortBy, user), PageRequest.of(page, size));
    }

    private Map<TmdbMovieDto, Boolean> toDtoMap(TmdbMovieSearchDto tmdbMovieSearchDto, User user) {
        return tmdbMovieSearchDto.getResults().stream()
                .collect(toMap(f -> f, f -> repository.existsByIdAndUsers(f.getId(), user), (f1, f2) -> f1,
                        LinkedHashMap::new));
    }
}
