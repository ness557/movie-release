package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.company.search.CompanySearchDto;
import com.ness.movie_release_web.service.tmdb.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/search")
    public ResponseEntity<CompanySearchDto> search(@RequestParam(value = "query") String query,
                                                   @CookieValue(value = "language", defaultValue = "en") Language language,
                                                   @RequestParam(value = "page", required = false) Integer page){
        if (page == null)
            page = 1;

        return ResponseEntity.ok(companyService.search(query, page, language).orElse(new CompanySearchDto()));
    }
}
