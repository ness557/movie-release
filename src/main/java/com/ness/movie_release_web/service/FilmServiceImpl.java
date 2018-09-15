package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmServiceImpl implements FilmService{

    @Autowired
    FilmOmdbService omdbService;

    @Autowired
    private FilmRepository repository;

    @Override
    public void save(Film film) {
        repository.save(film);
    }

    @Override
    public Film get(String title) {
        return repository.findByTitle(title);
    }

    @Override
    public Film get(Long id) {
        return repository.getOne(id);
    }

    @Override
    public void delete(Film film) {
        repository.delete(film);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(List<Film> films) {
        repository.deleteAll(films);
    }

    @Override
    public List<Film> getAll() {
        return repository.findAll();
    }

    @Override
    public List<Film> getByImdbIdAndUserId(String imdbId, Long userId) {
        return repository.findAllByImdbIdAndUserId(imdbId, userId);
    }

    @Override
    public boolean isExistsByImdbIdAndUserId(String imdbId, Long userId) {
        return repository.existsByImdbIdAndUserId(imdbId, userId);
    }

    @Scheduled(cron = "${cron.pattern.updateDB}")
    public void updateDB(){

        repository.getUniqueImdbIds().forEach(id -> {

            LocalDate dvdDate = omdbService.getInfo(id).getDvd();
            List<Film> allByImdbId = repository.findAllByImdbId(id);

            allByImdbId.forEach(e -> e.setDvdDate(dvdDate));
            repository.saveAll(allByImdbId);
        });
    }
}
