package com.ness.movie_release_web.controller;

import java.util.List;

import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;
import com.ness.movie_release_web.service.tmdb.TmdbNetworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/network")
@RequiredArgsConstructor
public class NetworkController {

    private final TmdbNetworkService tmdbNetworkService;

    @GetMapping("/search")
    public ResponseEntity<List<TmdbProductionCompanyDto>> search(@RequestParam(value = "query") String query){

        return ResponseEntity.ok(tmdbNetworkService.search(query));
    }
}
