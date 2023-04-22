package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

    public void addLike(Long id, Long userId) {
        log.info("addLike for film with id={} by user with id={}", id, userId);
        Film film = filmStorage.getById(id).orElseThrow(() -> {
            log.warn("film with id={} not exist", id);
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует.", id));
        });
        userStorage.getById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        log.info("deleteLike from film with id={} by user with id={}", id, userId);
        Film film = filmStorage.getById(id).orElseThrow(() -> {
            log.warn("film with id={} not exist", id);
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует.", id));
        });
        userStorage.getById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        log.info("getPopularFilms count={}", count);
        return filmStorage.getPopularFilms(count);
    }
}
