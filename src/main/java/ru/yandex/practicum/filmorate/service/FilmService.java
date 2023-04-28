package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilms() {
        log.info("getFilms");
        return filmStorage.getList();
    }

    public Film saveFilm(Film film) {
        log.info("saveFilm: {}", film);
        return filmStorage.save(film);
    }

    public Film updateFilm(Film film) {
        log.info("updateFilm: {}", film);
        return filmStorage.update(film);
    }

    public Film getFilmById(Long id) {
        log.info("getFilmById with id={}", id);
        return filmStorage.getById(id).orElseThrow(() -> {
            log.warn("film with id={} not exist", id);
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует.", id));
        });
    }

    public void deleteFilm(Long filmId) {
        log.info("deleteFilm with id={}", filmId);
        Film film = filmStorage.getById(filmId).orElseThrow(() -> {
            log.warn("film with id={} not exist", filmId);
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует.", filmId));
        });
        filmStorage.delete(filmId);
    }

    public Collection<Film> getSearchFilms(String query, List<String> by) {
        if (by.isEmpty()) {
            log.warn("Отсутствует категория поиска");
            return null;
        }
        if (query.isEmpty()) {
            log.warn("Отсутствует поисковый запрос");
            return null;
        }
        return filmStorage.getSearchFilms(query, by);
    }
}
