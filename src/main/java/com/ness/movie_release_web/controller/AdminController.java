package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private FilmService filmService;

    @PostMapping("/update_db")
    @ResponseBody
    public ResponseEntity updateDB(){
        filmService.updateDB();
        return ResponseEntity.ok().build();
    }

}
