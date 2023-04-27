package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
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

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        log.info("getCommonFilms userId={} friendId={}", userId, friendId);
        return likeStorage.getCommonFilms(userId, friendId);
    }
}
