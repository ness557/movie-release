package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.OmdbFullWrapper;
import com.ness.movie_release_web.model.wrapper.OmdbSearchResultWrapper;
import com.ness.movie_release_web.model.wrapper.OmdbWrapper;
import com.ness.movie_release_web.service.FilmOmdbService;
import com.ness.movie_release_web.service.FilmService;
import com.ness.movie_release_web.service.tmdb.TmdbDatesService;
import com.ness.movie_release_web.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/user")
@SessionAttributes(names = {"query", "year"}, types = {String.class, Integer.class})
public class FilmController {

    @Autowired
    private FilmOmdbService filmOmdbService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private TmdbDatesService tmdbDatesService;

    @GetMapping("/getFilm")
    public String getFilm(@RequestParam("imdbId") String imdbId,
                          Principal principal,
                          Model model) {

        User user = userService.findByLogin(principal.getName());

        if (filmService.isExistsByImdbIdAndUserId(imdbId, user.getId())) {
            model.addAttribute("subscribed", true);
        }
        model.addAttribute("film", filmOmdbService.getInfo(imdbId));
        model.addAttribute("releases", tmdbDatesService.getReleaseDates(imdbId));
        return "filmInfo";
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam(value = "imdbId") String imdbId,
                            Principal principal,
                            HttpServletRequest request) {

        String login = principal.getName();

        User user = userService.findByLogin(login);

        if (filmService.isExistsByImdbIdAndUserId(imdbId, user.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        OmdbFullWrapper wrapper = filmOmdbService.getInfo(imdbId);

        if (wrapper == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        Film film = new Film(null,
                wrapper.getImdbId(),
                LocalDateTime.now(),
                user);

        filmService.save(film);

        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping("/unSubscribe")
    public String unSubscribe(@RequestParam(value = "imdbId") String imdbId,
                              Principal principal,
                              HttpServletRequest request) {

        String login = principal.getName();

        User user = userService.findByLogin(login);

        filmService.getByImdbIdAndUserId(imdbId, user.getId()).forEach(filmService::delete);

        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(required = false, name = "year") Integer year,
                         @RequestParam(required = false, name = "page") Integer page,
                         Principal principal,
                         Model model) {

        User user = userService.findByLogin(principal.getName());

        // trim space at start
        query = StringUtils.trim(query);

        OmdbSearchResultWrapper result = filmOmdbService.search(query, year, page);

        //to save in session
        model.addAttribute("query", query);
        model.addAttribute("year", year);

        Map<OmdbWrapper, Boolean> filmsWithSubFlags = result.getFilms().stream()
                .collect(toMap(f -> f,
                               f -> filmService.isExistsByImdbIdAndUserId(f.getImdbId(), user.getId()),
                               (f1, f2) -> f1,
                               LinkedHashMap::new));

        model.addAttribute("films", filmsWithSubFlags);

        model.addAttribute("pageCount", (int) Math.ceil(result.getTotalResults() / 10.0));
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
        Page<Film> filmPage = filmService.getAllByUserWithPages(page, 10, user);
        List<Film> films = filmPage.getContent();

        List<OmdbFullWrapper> omdbFilms = films.stream().map(f -> filmOmdbService.getInfo(f.getImdbId())).collect(toList());

        model.addAttribute("botInitialized", !user.isTelegramNotify() || user.getTelegramChatId() != null);

        model.addAttribute("films", omdbFilms)
                .addAttribute("page", page)
                .addAttribute("pageCount", filmPage.getTotalPages());

        return "subscriptions";
    }
}
