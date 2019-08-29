package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.Mode;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.MovieDetailsDto;
import com.ness.movie_release_web.model.dto.tmdb.movie.details.Status;
import com.ness.movie_release_web.model.dto.tmdb.movie.discover.DiscoverSearchCriteria;
import com.ness.movie_release_web.model.dto.tmdb.movie.search.MovieSearchDto;
import com.ness.movie_release_web.model.dto.tmdb.movie.search.MovieDto;
import com.ness.movie_release_web.repository.MovieSortBy;
import com.ness.movie_release_web.service.FilmService;
import com.ness.movie_release_web.service.SubscriptionService;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.tmdb.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/movie")
@SessionAttributes(names = { "query", "year", "language", "mode" }, types = { String.class, Long.class,
        Language.class, Mode.class })
@RequiredArgsConstructor
public class MovieController {

    private final TmdbMovieServiceImpl movieService;
    private final FilmService filmService;
    private final UserService userService;
    private final TmdbDatesService tmdbDatesService;
    private final DiscoverService discoverService;
    private final GenreService genreService;
    private final CompanyService companyService;
    private final PeopleService peopleService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/{tmdbId}")
    public String getFilm(@PathVariable("tmdbId") Long tmdbId,
            @CookieValue(value = "language", defaultValue = "en") Language language,
            @CookieValue(value = "mode", defaultValue = "movie") Mode mode, Principal principal, Model model) {

        User user = userService.findByLogin(principal.getName());
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        if (filmService.isExistsByTmdbIdAndUser(tmdbId, user)) {
            model.addAttribute("subscribed", true);
        }

        Optional<MovieDetailsDto> movieDetails = movieService.getMovieDetails(tmdbId, language);
        if (!movieDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        model.addAttribute("film", movieDetails.get());
        model.addAttribute("releases", tmdbDatesService.getReleaseDates(tmdbId));
        return "movieInfo";
    }

    @PostMapping("/subscribe")
    @ResponseStatus(value = HttpStatus.OK)
    public void subscribe(@RequestParam(value = "tmdbId") Long tmdbId, Principal principal) {
        subscriptionService.subscribeToMovie(tmdbId, principal.getName());
    }

    @PostMapping("/unSubscribe")
    @ResponseStatus(value = HttpStatus.OK)
    public void unSubscribe(@RequestParam(value = "tmdbId") Long tmdbId, Principal principal) {
        subscriptionService.unsubscribeFromMovie(tmdbId, principal.getName());
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
            @RequestParam(required = false, name = "year") Long year,
            @RequestParam(required = false, name = "page") Integer page,
            @CookieValue(value = "language", defaultValue = "en") Language language,
            @CookieValue(value = "mode", defaultValue = "movie") Mode mode, Principal principal, Model model) {

        User user = userService.findByLogin(principal.getName());

        // trim space at start
        query = StringUtils.trim(query);

        // save in session
        model.addAttribute("query", query);
        model.addAttribute("year", year);
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        Optional<MovieSearchDto> optionalMovieSearch = movieService.searchForMovies(query, page, year, language);

        if (!optionalMovieSearch.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        MovieSearchDto movieSearchDto = optionalMovieSearch.get();

        model.addAttribute("films", toDtoMap(movieSearchDto, user));

        model.addAttribute("pageCount", movieSearchDto.getTotalPages());
        model.addAttribute("page", page);
        return "movieSearchResult";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<MovieSearchDto> searchApi(@RequestParam("query") String query,
                                                    @RequestParam(required = false, name = "year") Long year,
                                                    @RequestParam(required = false, name = "page") Integer page,
                                                    @CookieValue(value = "language", defaultValue = "en") Language language) {

        Optional<MovieSearchDto> optionalMovieSearch = movieService.searchForMovies(query, page, year, language);

        if (!optionalMovieSearch.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(optionalMovieSearch.get());
    }

    @GetMapping("/subscriptions")
    public String getSubs(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "statuses", required = false, defaultValue = "") List<Status> statuses,
            @RequestParam(value = "sortBy", required = false) MovieSortBy sortBy,
            @CookieValue(value = "subsMode", defaultValue = "false") String subsMode,
            @CookieValue(value = "language", defaultValue = "en") Language language,
            @CookieValue(value = "mode", defaultValue = "movie") Mode mode, HttpServletResponse response,
            Principal principal, Model model) {

        Boolean viewMode = Boolean.valueOf(subsMode);

        User user = userService.findByLogin(principal.getName());

        if (sortBy == null) {
            switch (language) {
            case en:
                sortBy = MovieSortBy.NameEn_asc;
                break;
            case ru:
                sortBy = MovieSortBy.NameRu_asc;
                break;
            }
        }

        // save in session
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        int size = viewMode ? 30 : 10;

        Page<Film> filmPage = filmService.getByUserAndStatusWithOrderbyAndPages(statuses, sortBy, user, page - 1, size);
        List<Film> films = filmPage.getContent();

        List<MovieDetailsDto> tmdbFilms;

        if (viewMode) {
            tmdbFilms = films.stream().map(f -> MovieDetailsDto.of(f, language)).collect(toList());
        } else {
            tmdbFilms = films.stream().map(f -> movieService.getMovieDetails(f.getId(), language))
                    .filter(Optional::isPresent).map(Optional::get).collect(toList());
        }

        model.addAttribute("statuses", statuses);
        model.addAttribute("sortBy", sortBy);

        model.addAttribute("botInitialized", !user.isTelegramNotify() || user.getTelegramChatId() != null);

        model.addAttribute("films", tmdbFilms).addAttribute("page", page).addAttribute("pageCount",
                filmPage.getTotalPages());

        model.addAttribute("asList", viewMode);

        return "movieSubscriptions";
    }

    @GetMapping("/discover")
    public String discover(@ModelAttribute("criteria") DiscoverSearchCriteria criteria,
            @RequestParam(value = "page", required = false) Integer page,
            @CookieValue(value = "language", defaultValue = "en") Language language,
            @CookieValue(value = "mode", defaultValue = "movie") Mode mode, Principal principal, Model model) {

        User user = userService.findByLogin(principal.getName());

        if (page == null)
            page = 1;

        // assigning values for service
        criteria.setPage(page);
        criteria.setLanguage(language);

        Optional<MovieSearchDto> optionalMovieSearch = discoverService.discoverMovie(criteria);

        if (optionalMovieSearch.isPresent()) {

            MovieSearchDto movieSearchDto = optionalMovieSearch.get();

            model.addAttribute("films", toDtoMap(movieSearchDto, user));

            model.addAttribute("pageCount",
                    movieSearchDto.getTotalPages() > 1000 ? 1000 : movieSearchDto.getTotalPages());
            model.addAttribute("page", page);
        }

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        // adding all attributes to form
        model.addAttribute("companies", companyService.getCompanies(criteria.getCompanies(), language));
        model.addAttribute("people", peopleService.getPeopleList(criteria.getPeople(), language));
        model.addAttribute("criteria", criteria);

        // adding genres to form
        model.addAttribute("genres", genreService.getMovieGenres(language));

        return "discoverMovies";
    }

    private Map<MovieDto, Boolean> toDtoMap(MovieSearchDto movieSearchDto, User user) {
        return movieSearchDto.getResults().stream()
                .collect(toMap(f -> f, f -> filmService.isExistsByTmdbIdAndUser(f.getId(), user), (f1, f2) -> f1,
                        LinkedHashMap::new));
    }
}
