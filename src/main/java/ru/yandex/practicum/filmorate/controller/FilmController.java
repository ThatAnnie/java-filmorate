package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final LikeService likeService;
    private final DirectorService directorService;

    @Autowired
    public FilmController(FilmService filmService, LikeService likeService, DirectorService directorService) {
        this.filmService = filmService;
        this.likeService = likeService;
        this.directorService = directorService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public Film saveFilm(@RequestBody @Valid Film film) {
        return filmService.saveFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        likeService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        likeService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return likeService.getPopularFilms(count);
    }


    @GetMapping("/director/{directorId}")
    public Collection<Film> getSortedFilms(@PathVariable Long directorId,
                                           @RequestParam String sortBy) {
        if (directorService.getDirectorById(directorId).isEmpty()) {
            throw new EntityNotExistException("directorId is not exist");
        }
        if (sortBy.equals("year")) {
            return filmService.getSortedFilmByYear(directorId);
        }
        if (sortBy.equals("likes")) {
            return likeService.getSortedFilmByLikesDirector(directorId);
        }
        return new ArrayList<>();
}

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable Long filmId) {
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getSortedFilms(@PathVariable Long directorId,
                                           @RequestParam String sortBy) {
        if (directorService.getDirectorById(directorId).isEmpty()) {
            throw new EntityNotExistException("directorId is not exist");
        }
        if (sortBy.equals("year")) {
            return filmService.getSortedFilmByYear(directorId);
        }
        if (sortBy.equals("likes")) {
            return likeService.getSortedFilmByLikesDirector(directorId);
        }
        return new ArrayList<>();
    }
}
