package com.ness.movie_release_web.controller;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.model.UserTVSeries;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.Mode;
import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.DiscoverSearchCriteria;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.TVDetailsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.search.TVSearchWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.search.TVWrapper;
import com.ness.movie_release_web.service.TVSeriesService;
import com.ness.movie_release_web.service.UserService;
import com.ness.movie_release_web.service.tmdb.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Controller
@RequestMapping("/series")
@SessionAttributes(names = {"query", "year", "language", "mode"},
        types = {String.class, Integer.class, Language.class, Mode.class})
public class TVSeriesController {

    @Autowired
    private UserService userService;

    @Autowired
    private TVSeriesService dbSeriesService;

    @Autowired
    private TmdbTVSeriesService tmdbSeriesService;

    @Autowired
    private DiscoverService discoverService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private GenreService genreService;

    @GetMapping("/getSeries")
    public String getFilm(@RequestParam("tmdbId") Integer tmdbId,
                          Principal principal,
                          Model model) {

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();
        Mode mode = user.getMode();
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        if (dbSeriesService.isExistsByTmdbIdAndUserId(tmdbId, user.getId())) {
            model.addAttribute("subscribed", true);
        }

        Optional<TVDetailsWrapper> tvDetails = tmdbSeriesService.getTVDetails(tmdbId, language);
        if (!tvDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        model.addAttribute("series", tvDetails.get());

        return "seriesInfo";
    }

    @PostMapping("/subscribe")
    public ResponseEntity subscribe(@RequestParam(value = "tmdbId") Integer tmdbId,
                                    Principal principal) {

        String login = principal.getName();
        User user = userService.findByLogin(login);

        Optional<TVDetailsWrapper> optionalTvDetails = tmdbSeriesService.getTVDetails(tmdbId, Language.en);

        if (!optionalTvDetails.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);

        dbSeriesService.subscribeUser(tmdbId, user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/unSubscribe")
    public ResponseEntity unSubscribe(@RequestParam(value = "tmdbId") Integer tmdbId,
                                      Principal principal) {

        String login = principal.getName();
        User user = userService.findByLogin(login);

        dbSeriesService.unSubscribeUser(tmdbId, user);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query,
                         @RequestParam(required = false, name = "year") Integer year,
                         @RequestParam(required = false, name = "page") Integer page,
                         Principal principal,
                         Model model) {

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();
        Mode mode = user.getMode();

        // trim space at start
        query = StringUtils.trim(query);

        //save in session
        model.addAttribute("query", query);
        model.addAttribute("year", year);
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        Optional<TVSearchWrapper> optionalSearchResult = tmdbSeriesService.search(query, page, year, language);

        if (!optionalSearchResult.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        TVSearchWrapper searchWrapper = optionalSearchResult.get();

        Map<TVWrapper, Boolean> filmsWithSubFlags = searchWrapper.getResults().stream()
                .collect(toMap(
                        f -> f,
                        f -> dbSeriesService.isExistsByTmdbIdAndUserId(f.getId(), user.getId()),
                        (f1, f2) -> f1,
                        LinkedHashMap::new));

        model.addAttribute("series", filmsWithSubFlags);

        model.addAttribute("pageCount", searchWrapper.getTotalPages());
        model.addAttribute("page", page);

        return "seriesSearchResult";
    }

    @GetMapping("/subscriptions")
    public String getSubs(@RequestParam(value = "page", required = false) Integer page,
                          Principal principal,
                          Model model) {

        if (page == null)
            page = 0;

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();
        Mode mode = user.getMode();

        //save in session
        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        Page<UserTVSeries> userTVSeries = dbSeriesService.getAllByUserWithPages(page, 10, user);
        List<UserTVSeries> series = userTVSeries.getContent();


        List<TVDetailsWrapper> subscriptions = series.stream()
                .map(f -> tmdbSeriesService.getTVDetails(f.getId().getTvSeriesId().intValue(), language))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());

        model.addAttribute("botInitialized", !user.isTelegramNotify() || user.getTelegramChatId() != null);

        model.addAttribute("series", subscriptions)
                .addAttribute("page", page)
                .addAttribute("pageCount", userTVSeries.getTotalPages());

        return "seriesSubscriptions";
    }

    @GetMapping("/discover")
    public String discover(@ModelAttribute("criteria") DiscoverSearchCriteria criteria,
                           @RequestParam(value = "page", required = false) Integer page,
                           Principal principal,
                           Model model) {

        User user = userService.findByLogin(principal.getName());
        Language language = user.getLanguage();
        Mode mode = user.getMode();

        if (page == null)
            page = 1;

        // assigning values for service
        criteria.setPage(page);
        criteria.setLanguage(language);

        Optional<TVSearchWrapper> optionalMovieSearch = discoverService.discoverSeries(criteria);

        if (optionalMovieSearch.isPresent()) {

            TVSearchWrapper movieSearchWrapper = optionalMovieSearch.get();

            Map<TVWrapper, Boolean> filmsWithSubFlags = movieSearchWrapper.getResults().stream()
                    .collect(toMap(f -> f,
                            f -> dbSeriesService.isExistsByTmdbIdAndUserId(f.getId(), user.getId()),
                            (f1, f2) -> f1,
                            LinkedHashMap::new));
            model.addAttribute("series", filmsWithSubFlags);

            model.addAttribute("pageCount", movieSearchWrapper.getTotalPages());
            model.addAttribute("page", page);
        }

        model.addAttribute("language", language);
        model.addAttribute("mode", mode);

        // adding all attributes to form
        model.addAttribute("companies", companyService.getCompanies(criteria.getCompanies(), language));
        model.addAttribute("criteria", criteria);

        // adding genreWrappers to form
        model.addAttribute("genres", genreService.getTVGenres(language));

        return "discoverSeries";
    }
}
