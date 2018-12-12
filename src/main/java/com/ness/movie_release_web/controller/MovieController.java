package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.Mode;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.Status;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.DiscoverSearchCriteria;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearchWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieWrapper;
import com.ness.movie_release_web.repository.MovieSortBy;
import com.ness.movie_release_web.service.FilmService;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.tmdb.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/movie")
@SessionAttributes(names = {"query", "year", "language", "mode"},
        types = {String.class, Integer.class, Language.class, Mode.class})
public class MovieController {

    @Autowired
    private MovieServiceImpl movieService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private TmdbDatesService tmdbDatesService;

    @Autowired
    private DiscoverService discoverService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PeopleService peopleService;

    @GetMapping("/{tmdbId}")
    public String getFilm(@PathVariable("tmdbId") Integer tmdbId,
                          Principal principal,
                          Model model) {

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();
        Mode mode = user.getMode();
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        if (filmService.isExistsByTmdbIdAndUser(tmdbId, user)) {
            model.addAttribute("subscribed", true);
        }

        Optional<MovieDetailsWrapper> movieDetails = movieService.getMovieDetails(tmdbId, language);
        if (!movieDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        model.addAttribute("film", movieDetails.get());
        model.addAttribute("releases", tmdbDatesService.getReleaseDates(tmdbId));
        return "movieInfo";
    }

    @PostMapping("/subscribe")
    public ResponseEntity subscribe(@RequestParam(value = "tmdbId") Integer tmdbId,
                                    Principal principal,
                                    HttpServletRequest request) {

        String login = principal.getName();

        User user = userService.findByLogin(login);

        if (filmService.isExistsByTmdbIdAndUser(tmdbId, user))
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        Optional<MovieDetailsWrapper> optionalMovieDetails = movieService.getMovieDetails(tmdbId, Language.en);

        if (!optionalMovieDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        MovieDetailsWrapper movieDetailsWrapper = optionalMovieDetails.get();

        Optional<Film> filmOpt = filmService.findById(tmdbId.longValue());

        Film film = filmOpt.orElse(new Film(
                movieDetailsWrapper.getId().longValue(),
                movieDetailsWrapper.getTitle(),
                "",
                movieDetailsWrapper.getStatus(),
                movieDetailsWrapper.getReleaseDate(),
                movieDetailsWrapper.getVoteAverage().floatValue(),
                new ArrayList<>()));
        film.getUsers().add(user);

        filmService.save(film);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/unSubscribe")
    public ResponseEntity unSubscribe(@RequestParam(value = "tmdbId") Integer tmdbId,
                                      Principal principal) {

        String login = principal.getName();

        User user = userService.findByLogin(login);

        Optional<Film> film = filmService.getByTmdbIdAndUser(tmdbId, user);
        film.ifPresent(f -> {
            f.getUsers().remove(user);
            filmService.save(f);
        });

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(required = false, name = "year") Integer year,
                         @RequestParam(required = false, name = "page") Integer page,
                         Principal principal,
                         Model model) {

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();
        Mode mode = user.getMode();

        // trim space at start
        query = StringUtils.trim(query);

        //save in session
        model.addAttribute("query", query);
        model.addAttribute("year", year);
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        Optional<MovieSearchWrapper> optionalMovieSearch = movieService.searchForMovies(query, page, year, language);

        if (!optionalMovieSearch.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        MovieSearchWrapper movieSearchWrapper = optionalMovieSearch.get();

        Map<MovieWrapper, Boolean> filmsWithSubFlags = movieSearchWrapper.getResults().stream()
                .collect(toMap(f -> f,
                        f -> filmService.isExistsByTmdbIdAndUser(f.getId(), user),
                        (f1, f2) -> f1,
                        LinkedHashMap::new));
        model.addAttribute("films", filmsWithSubFlags);

        model.addAttribute("pageCount", movieSearchWrapper.getTotalPages());
        model.addAttribute("page", page);
        return "movieSearchResult";
    }

    @GetMapping("/subscriptions")
    public String getSubs(@RequestParam(value = "page", required = false) Integer page,
                          @RequestParam(value = "statuses", required = false) List<Status> statuses,
                          @RequestParam(value = "sortBy", required = false) MovieSortBy sortBy,
                          Principal principal,
                          Model model) {

        if (page == null)
            page = 0;

        if (statuses == null) {
            statuses = emptyList();
        }

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();
        Mode mode = user.getMode();

        if(sortBy == null){
            switch (language) {
                case en:
                    sortBy = MovieSortBy.NameEn_asc;
                    break;
                case ru:
                    sortBy = MovieSortBy.NameRu_asc;
                    break;
            }
        }

        //save in session
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        Page<Film> filmPage = filmService.getByUserAndStatusWithOrderbyAndPages(statuses, sortBy, user, page, 10);
        List<Film> films = filmPage.getContent();

        List<MovieDetailsWrapper> tmdbFilms =
                films.stream()
                        .map(f -> movieService.getMovieDetails(f.getId().intValue(), language))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toList());

        model.addAttribute("statuses", statuses);
        model.addAttribute("sortBy", sortBy);

        model.addAttribute("botInitialized", !user.isTelegramNotify() || user.getTelegramChatId() != null);

        model.addAttribute("films", tmdbFilms)
                .addAttribute("page", page)
                .addAttribute("pageCount", filmPage.getTotalPages());
        return "movieSubscriptions";
    }

    @GetMapping("/discover")
    public String discover(@ModelAttribute("criteria") DiscoverSearchCriteria criteria,
                           @RequestParam(value = "page", required = false) Integer page,
                           Principal principal,
                           Model model) {

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();
        Mode mode = user.getMode();

        if (page == null)
            page = 1;

        // assigning values for service
        criteria.setPage(page);
        criteria.setLanguage(language);

        Optional<MovieSearchWrapper> optionalMovieSearch = discoverService.discoverMovie(criteria);

        if (optionalMovieSearch.isPresent()) {

            MovieSearchWrapper movieSearchWrapper = optionalMovieSearch.get();

            Map<MovieWrapper, Boolean> filmsWithSubFlags = movieSearchWrapper.getResults().stream()
                    .collect(toMap(f -> f,
                            f -> filmService.isExistsByTmdbIdAndUser(f.getId(), user),
                            (f1, f2) -> f1,
                            LinkedHashMap::new));
            model.addAttribute("films", filmsWithSubFlags);

            model.addAttribute("pageCount", movieSearchWrapper.getTotalPages() > 1000 ? 1000 : movieSearchWrapper.getTotalPages());
            model.addAttribute("page", page);
        }

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        // adding all attributes to form
        model.addAttribute("companies", companyService.getCompanies(criteria.getCompanies(), language));
        model.addAttribute("people", peopleService.getPeopleList(criteria.getPeople(), language));
        model.addAttribute("criteria", criteria);

        // adding genreWrappers to form
        model.addAttribute("genres", genreService.getMovieGenres(language));

        return "discoverMovies";
    }
}
