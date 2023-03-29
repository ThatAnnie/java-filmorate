package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private InMemoryFilmStorage filmRepository= new InMemoryFilmStorage();

    @GetMapping
    public List<Film> getFilms() {
        log.info("getFilms");
        return filmRepository.getFilmList();
    }

    @PostMapping
    public Film saveFilm(@RequestBody @Valid Film film) {
        filmRepository.saveFilm(film);
        log.info("saveFilm: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("updateFilm: {}", film);
        return filmRepository.updateFilm(film);
    }
}
