package com.ness.movie_release_web.controller;

import java.util.List;

import com.ness.movie_release_web.model.dto.tmdb.ProductionCompanyDto;
import com.ness.movie_release_web.service.tmdb.NetworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/network")
@RequiredArgsConstructor
public class NetworkController {

    private final NetworkService networkService;

    @GetMapping("/search")
    public ResponseEntity<List<ProductionCompanyDto>> search(@RequestParam(value = "query") String query){

        return ResponseEntity.ok(networkService.search(query));
    }
}
