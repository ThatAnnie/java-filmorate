package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class LikeService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final DirectorStorage directorStorage;
    private final EventService eventService;

    @Autowired
    public LikeService(FilmStorage filmStorage, UserStorage userStorage, LikeStorage likeStorage, DirectorStorage directorStorage, EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.directorStorage = directorStorage;
        this.eventService = eventService;
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
        likeStorage.addLike(id, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.ADD, id);
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
        likeStorage.deleteLike(id, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.REMOVE, id);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        log.info("getPopularFilms count={}", count);
        return likeStorage.getPopularFilms(count);
    }

    public Collection<Film> getSortedFilmByLikesDirector(Long dirId) {
        directorStorage.getById(dirId).orElseThrow(() -> {
            log.warn("director with id={} not exist", dirId);
            throw new EntityNotExistException(String.format("Режиссер с id=%d не существует.", dirId));
        });
        return likeStorage.getSortedFilmByLikesDirector(dirId);
    }

    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        log.info("getCommonFilms userId={} friendId={}", userId, friendId);
        return likeStorage.getCommonFilms(userId, friendId);
    }
}
