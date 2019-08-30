package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.Mode;
import com.ness.movie_release_web.dto.tmdb.people.TmdbPeopleDto;
import com.ness.movie_release_web.service.tmdb.TmdbPeopleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/people")
@RequiredArgsConstructor
public class PeopleController {

    private final TmdbPeopleService tmdbPeopleService;

    @GetMapping("/search")
    public ResponseEntity<List<TmdbPeopleDto>> search(@RequestParam(value = "query") String query) {

        return ResponseEntity.ok(tmdbPeopleService.search(query));
    }

    @GetMapping("/{tmdbId}")
    public String getDetails(@PathVariable("tmdbId") Long id,
                             @CookieValue(value = "language", defaultValue = "en") Language language,
                             @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                             Model model) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute("person",
                tmdbPeopleService.getDetails(id, language)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return "personInfo";
    }
}
