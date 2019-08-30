package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.company.search.TmdbCompanySearchDto;
import com.ness.movie_release_web.service.tmdb.TmdbCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final TmdbCompanyService tmdbCompanyService;

    @GetMapping("/search")
    public ResponseEntity<TmdbCompanySearchDto> search(@RequestParam(value = "query") String query,
                                                       @CookieValue(value = "language", defaultValue = "en") Language language,
                                                       @RequestParam(value = "page", required = false, defaultValue = "1") Integer page){
        return ResponseEntity.ok(tmdbCompanyService.search(query, page, language).orElse(new TmdbCompanySearchDto()));
    }
}
