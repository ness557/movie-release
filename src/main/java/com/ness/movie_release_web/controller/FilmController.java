package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.OmdbFullWrapper;
import com.ness.movie_release_web.model.OmdbSearchResultWrapper;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.service.FilmOmdbService;
import com.ness.movie_release_web.service.FilmService;
import com.ness.movie_release_web.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

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

    @GetMapping("/getFilm")
    public String getFilm(@RequestParam("imdbId") String imdbId,
                          Principal principal,
                          Model model){

        User user = userService.findByLogin(principal.getName());

        if (filmService.isExistsByImdbIdAndUserId(imdbId, user.getId())) {
            model.addAttribute("subscribed", true);
        }
        model.addAttribute("film", filmOmdbService.getInfo(imdbId));
        return "filmInfo";
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam(value = "imdbId") String imdbId,
                            Principal principal,
                            Model model){

        String login = principal.getName();

        User user = userService.findByLogin(login);

        if(filmService.isExistsByImdbIdAndUserId(imdbId, user.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        OmdbFullWrapper wrapper = filmOmdbService.getInfo(imdbId);

        if (wrapper == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        Film film = new Film(null,
                wrapper.getImdbId(),
                wrapper.getTitle(),
                wrapper.getPoster(),
                wrapper.getType(),
                wrapper.getReleased(),
                wrapper.getDvd(),
                user);

        filmService.save(film);

        return "redirect:getFilm?imdbId=" + imdbId;
    }

    @PostMapping("/unSubscribe")
    public String unSubscribe(@RequestParam(value = "imdbId") String imdbId,
                            Principal principal){

        String login = principal.getName();

        User user = userService.findByLogin(login);

        filmService.getByImdbIdAndUserId(imdbId, user.getId()).forEach(filmService::delete);

        return "redirect:getFilm?imdbId=" + imdbId;
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(required = false, name = "year") Integer year,
                         @RequestParam(required = false, name = "page") Integer page,
                         Model model){

        OmdbSearchResultWrapper result;

        // trim space at start
        query = StringUtils.trim(query);

        result = filmOmdbService.search(query, year, page);

        //to save in at session
        model.addAttribute("query", query);
        model.addAttribute("year", year);

        model.addAttribute("films", result.getFilms());
        model.addAttribute("pageCount", (int) Math.ceil(result.getTotalResults()/10.0));
        model.addAttribute("page", page);

        return "searchResult";
    }
}
