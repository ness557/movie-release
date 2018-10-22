package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {

//    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FilmRepository repository;

    @Override
    public void save(Film film) {
        repository.save(film);
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
    public Page<Film> getAllByUserWithPages(Integer page, Integer size, User user) {
        return repository.findAllByUserOrderBySubscriptionDateDesc(user, PageRequest.of(page, size));
    }

    @Override
    public List<Film> getByImdbIdAndUserId(String imdbId, Long userId) {
        return repository.findAllByImdbIdAndUserId(imdbId, userId);
    }

    @Override
    public boolean isExistsByImdbIdAndUserId(String imdbId, Long userId) {
        return repository.existsByImdbIdAndUserId(imdbId, userId);
    }
}
