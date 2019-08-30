package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.Mode;
import com.ness.movie_release_web.dto.MovieDiscoverDto;
import com.ness.movie_release_web.dto.MovieDto;
import com.ness.movie_release_web.dto.MovieSearchDto;
import com.ness.movie_release_web.dto.MovieSubscriptionsDto;
import com.ness.movie_release_web.dto.tmdb.movie.details.Status;
import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieSearchDto;
import com.ness.movie_release_web.repository.MovieSortBy;
import com.ness.movie_release_web.service.MovieService;
import com.ness.movie_release_web.service.SubscriptionService;
import com.ness.movie_release_web.service.tmdb.TmdbMovieServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/movie")
@RequiredArgsConstructor
public class MovieController {

    private final TmdbMovieServiceImpl tmdbMovieService;
    private final MovieService movieService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/{tmdbId}")
    public String getFilm(@PathVariable("tmdbId") Long tmdbId,
                          @CookieValue(value = "language", defaultValue = "en") Language language,
                          @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                          Principal principal,
                          Model model) {

        MovieDto movie = movieService.getMovie(tmdbId, principal.getName(), language);

        model.addAttribute("subscribed", movie.getSubscribed());
        model.addAttribute("releases", movie.getReleases());
        model.addAttribute("film", movie.getMovieDetailsDto());
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        return "movieInfo";
    }

    @PostMapping("/subscribe")
    @ResponseStatus(value = HttpStatus.OK)
    public void subscribe(@RequestParam(value = "tmdbId") Long tmdbId, Principal principal) {
        subscriptionService.subscribeToMovie(tmdbId, principal.getName());
    }

    @PostMapping("/unSubscribe")
    @ResponseStatus(value = HttpStatus.OK)
    public void unSubscribe(@RequestParam(value = "tmdbId") Long tmdbId, Principal principal) {
        subscriptionService.unsubscribeFromMovie(tmdbId, principal.getName());
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(required = false, name = "year") Long year,
                         @RequestParam(required = false, name = "page") Long page,
                         @CookieValue(value = "language", defaultValue = "en") Language language,
                         @CookieValue(value = "mode", defaultValue = "movie") Mode mode, Principal principal, Model model) {


        // trim space at start
        query = StringUtils.trim(query);

        MovieSearchDto searchDto = movieService.search(query, page, year, language, principal.getName());

        model.addAttribute("query", query);
        model.addAttribute("year", year);
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);
        model.addAttribute("films", searchDto.getMovieSearchDtoMap());
        model.addAttribute("pageCount", searchDto.getTotal());
        model.addAttribute("page", page);

        return "movieSearchResult";
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<TmdbMovieSearchDto> searchApi(@RequestParam("query") String query,
                                                        @RequestParam(required = false, name = "year") Long year,
                                                        @RequestParam(required = false, name = "page") Long page,
                                                        @CookieValue(value = "language", defaultValue = "en") Language language) {

        return ResponseEntity.ok(
                tmdbMovieService.searchForMovies(query, page, year, language)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT)));
    }

    @GetMapping("/subscriptions")
    public String getSubs(@RequestParam(value = "page", required = false, defaultValue = "1") Long page,
                          @RequestParam(value = "statuses", required = false, defaultValue = "") List<Status> statuses,
                          @RequestParam(value = "sortBy", required = false) MovieSortBy sortBy,
                          @CookieValue(value = "viewMode", defaultValue = "false") Boolean viewMode,
                          @CookieValue(value = "language", defaultValue = "en") Language language,
                          @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                          Principal principal,
                          Model model) {

        MovieSubscriptionsDto subscriptionsDto =
                movieService.getSubscriptions(statuses, sortBy, viewMode, page, principal.getName(), language);

        model.addAttribute("language", language)
                .addAttribute("mode", mode)
                .addAttribute("statuses", statuses)
                .addAttribute("sortBy", subscriptionsDto.getSortBy())
                .addAttribute("botInitialized", subscriptionsDto.getBotInitialized())
                .addAttribute("movies", subscriptionsDto.getMovieDetailsDtos())
                .addAttribute("page", page)
                .addAttribute("pageCount", subscriptionsDto.getTotalPages())
                .addAttribute("asList", viewMode);

        return "movieSubscriptions";
    }

    @GetMapping("/discover")
    public String discover(@ModelAttribute("criteria") TmdbDiscoverSearchCriteria criteria,
                           @RequestParam(value = "page", required = false, defaultValue = "1") Long page,
                           @CookieValue(value = "language", defaultValue = "en") Language language,
                           @CookieValue(value = "mode", defaultValue = "movie") Mode mode,
                           Principal principal,
                           Model model) {

        // assigning values for service
        criteria.setPage(page.intValue());
        criteria.setLanguage(language);

        MovieDiscoverDto discoverDto = movieService.discover(criteria, principal.getName());

        // adding all attributes to form
        model.addAttribute("criteria", criteria);
        model.addAttribute("companies", discoverDto.getCompanies());
        model.addAttribute("people", discoverDto.getPeople());
        model.addAttribute("genres", discoverDto.getGenres());

        model.addAttribute("films", discoverDto.getTmdbMovieDtoMap());
        model.addAttribute("pageCount", discoverDto.getTotalPages());

        model.addAttribute("page", page);
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        return "discoverMovies";
    }
}
