package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.Mode;
import com.ness.movie_release_web.model.dto.tmdb.people.PeopleDto;
import com.ness.movie_release_web.service.tmdb.PeopleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final PeopleService peopleService;

    @GetMapping("/search")
    public ResponseEntity<List<PeopleDto>> search(@RequestParam(value = "query") String query) {

        return ResponseEntity.ok(peopleService.search(query));
    }

    @GetMapping("/{tmdbId}")
    public String getDetails(@PathVariable("tmdbId") Long id,
                             @CookieValue(value = "language", defaultValue = "en") Language language,
                             @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                             Model model,
                             Principal principal) {

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        Optional<PeopleDto> peopleOpt = peopleService.getDetails(id, language);
        if (!peopleOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("person", peopleOpt.get());

        return "personInfo";
    }
}
