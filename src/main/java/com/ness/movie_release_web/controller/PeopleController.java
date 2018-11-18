package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.service.tmdb.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/people")
public class PeopleController {

    @Autowired
    private PeopleService peopleService;

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(value = "query") String query){

        return ResponseEntity.ok(peopleService.search(query));
    }
}
