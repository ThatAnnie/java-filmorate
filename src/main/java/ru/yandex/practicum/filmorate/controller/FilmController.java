package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final LikeService likeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film saveFilm(@RequestBody @Valid Film film) {
        return filmService.saveFilm(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        likeService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        likeService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                            @RequestParam(required = false) Integer genreId,
                                            @RequestParam(required = false) Integer year) {
        return likeService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getSortedFilms(@PathVariable Long directorId,
                                           @RequestParam String sortBy) {
        return filmService.getSortedFilms(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Long filmId) {
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return likeService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getSearchFilms(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "by", required = false) List<String> by) {
        return filmService.getSearchFilms(query, by);
    }
}
