package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.MovieDetails;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.Movie;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.search.MovieSearch;
import com.ness.movie_release_web.service.FilmOmdbService;
import com.ness.movie_release_web.service.FilmService;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.tmdb.MovieServiceImpl;
import com.ness.movie_release_web.service.tmdb.TmdbDatesService;
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
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/user")
@SessionAttributes(names = {"query", "year", "language"}, types = {String.class, Integer.class, Language.class})
public class FilmController {

    @Autowired
    private FilmOmdbService filmOmdbService;

    @Autowired
    private MovieServiceImpl movieService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private TmdbDatesService tmdbDatesService;

    @GetMapping("/getFilm")
    public String getFilm(@RequestParam("tmdbId") Integer tmdbId,
                          Principal principal,
                          Model model) {

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();
        model.addAttribute("language", language);

        if (filmService.isExistsByTmdbIdAndUserId(tmdbId, user.getId())) {
            model.addAttribute("subscribed", true);
        }

        // TODO rewrite ui
        model.addAttribute("film", movieService.getMovieDetails(tmdbId, language));
        model.addAttribute("releases", tmdbDatesService.getReleaseDates(tmdbId));
        return "filmInfo";
    }

    @PostMapping("/subscribe")
    public ResponseEntity subscribe(@RequestParam(value = "tmdbId") Integer tmdbId,
                            Principal principal,
                            HttpServletRequest request) {

        String login = principal.getName();

        User user = userService.findByLogin(login);

        if (filmService.isExistsByTmdbIdAndUserId(tmdbId, user.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        Optional<MovieDetails> optionalMovieDetails = movieService.getMovieDetails(tmdbId, Language.en);

        if (!optionalMovieDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        MovieDetails movieDetails = optionalMovieDetails.get();

        Film film = new Film(null,
                movieDetails.getId(),
                LocalDateTime.now(),
                user);

        filmService.save(film);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unSubscribe")
    public ResponseEntity unSubscribe(@RequestParam(value = "tmdbId") Integer tmdbId,
                              Principal principal,
                              HttpServletRequest request) {

        String login = principal.getName();

        User user = userService.findByLogin(login);

        filmService.getByTmdbIdAndUserId(tmdbId, user.getId()).forEach(filmService::delete);

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

        // trim space at start
        query = StringUtils.trim(query);

        //save in session
        model.addAttribute("query", query);
        model.addAttribute("year", year);
        model.addAttribute("language", language);

        Optional<MovieSearch> optionalMovieSearch = movieService.searchForMovies(query, page, year, language);

        if (!optionalMovieSearch.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        MovieSearch movieSearch = optionalMovieSearch.get();

        Map<Movie, Boolean> filmsWithSubFlags = movieSearch.getResults().stream()
                .collect(toMap(f -> f,
                               f -> filmService.isExistsByTmdbIdAndUserId(f.getId(), user.getId()),
                               (f1, f2) -> f1,
                               LinkedHashMap::new));
        model.addAttribute("films", filmsWithSubFlags);

        model.addAttribute("pageCount", movieSearch.getTotalPages());
        model.addAttribute("page", page);
        return "searchResult";
    }

    @GetMapping("/subscriptions")
    public String getSubs(@RequestParam(value = "page", required = false) Integer page,
                          Principal principal,
                          Model model) {

        if (page == null)
            page = 0;

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();

        //save in session
        model.addAttribute("language", language);

        Page<Film> filmPage = filmService.getAllByUserWithPages(page, 10, user);
        List<Film> films = filmPage.getContent();

        List<MovieDetails> tmdbFilms =
                films.stream()
                      .map(f -> movieService.getMovieDetails(f.getTmdbId(), language))
                      .filter(Optional::isPresent)
                      .map(Optional::get)
                      .collect(toList());

        model.addAttribute("botInitialized", !user.isTelegramNotify() || user.getTelegramChatId() != null);

        model.addAttribute("films", tmdbFilms)
                .addAttribute("page", page)
                .addAttribute("pageCount", filmPage.getTotalPages());
        return "subscriptions";
    }

    @PostMapping("/setLanguage")
    public ResponseEntity setLanguage(@RequestParam(value = "language") Language language, Principal principal, Model model){
        User user = userService.findByLogin(principal.getName());
        user.setLanguage(language);
        userService.save(user);

        model.addAttribute("language",language);

        return ResponseEntity.ok().build();
    }
}
