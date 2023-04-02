package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final Storage<Film> filmStorage;
    private final Storage<User> userStorage;

    @Autowired
    public FilmService(Storage<Film> filmStorage, Storage<User> userStorage) {
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
        film.getUsersLikes().add(userId);
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
        film.getUsersLikes().remove(userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        log.info("getPopularFilms count={}", count);
        return getFilms().stream()
                .sorted(Comparator.comparing(Film::getNumberOfLikes).reversed())
                .limit(Objects.requireNonNullElse(count, 10))
                .collect(Collectors.toList());
    }
}
