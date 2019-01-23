package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.Mode;
import com.ness.movie_release_web.model.wrapper.tmdb.people.PeopleWrapper;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.tmdb.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/people")
public class PeopleController {

    @Autowired
    private PeopleService peopleService;

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(value = "query") String query) {

        return ResponseEntity.ok(peopleService.search(query));
    }

    @GetMapping("/{tmdbId}")
    public String getDetails(@PathVariable("tmdbId") Integer id,
                             @CookieValue(value = "language", defaultValue = "en") Language language,
                             @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                             Model model,
                             Principal principal) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        Optional<PeopleWrapper> peopleOpt = peopleService.getDetails(id, language);
        if (!peopleOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("person", peopleOpt.get());

        return "personInfo";
    }
}
