package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.company.search.CompanySearchWrapper;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.tmdb.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public ResponseEntity search(@RequestParam(value = "query") String query,
                                           @RequestParam(value = "page", required = false) Integer page,
                                           Principal principal){
        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();

        if (page == null)
            page = 1;

        return ResponseEntity.ok(companyService.search(query, page, language).orElse(new CompanySearchWrapper()));
    }
}
