package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final LikeService likeService;

    @Autowired
    public FilmService(FilmStorage filmStorage, DirectorStorage directorStorage, LikeService likeService) {
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
        this.likeService = likeService;
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

    public Collection<Film> getSortedFilmByYear(Long dirId) {
        directorStorage.getById(dirId).orElseThrow(() -> {
            log.warn("director with id={} not exist", dirId);
            throw new EntityNotExistException(String.format("Режиссер с id=%d не существует.", dirId));
        });
        return filmStorage.getFilmsByDirId(dirId).stream()
                .sorted(Comparator.comparingInt(o -> o.getReleaseDate().getYear()))
                .collect(Collectors.toList());
    }

    public void deleteFilm(Long filmId) {
        log.info("deleteFilm with id={}", filmId);
        Film film = filmStorage.getById(filmId).orElseThrow(() -> {
            log.warn("film with id={} not exist", filmId);
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует.", filmId));
        });
        filmStorage.delete(filmId);
    }

    public Collection<Film> getSortedFilms(Long directorId, String sortBy) {
        if (sortBy.equals("year")) {
            return getSortedFilmByYear(directorId);
        } else if (sortBy.equals("likes")) {
            return likeService.getSortedFilmByLikesDirector(directorId);
        } else {
            throw new IllegalArgumentException("Не задан параметр сортировки");
        }

    }

    public Collection<Film> getSearchFilms(String query, List<String> by) {
        if (by.isEmpty()) {
            log.warn("Отсутствует категория поиска");
            throw new EntityNotExistException("Отсутствует категория поиска");
        }
        if (by.size() > 2) {
            log.warn("В категории поиска более двух значений");
            throw new EntityNotExistException("В категории поиска более двух значений");
        }
        List<String> test = new ArrayList<>();
        test.add("title");
        test.add("director");
        if ((by.size() == 2) & !by.containsAll(test)) {
            log.warn("В категории поиска неверные параметры");
            throw new EntityNotExistException("В категории поиска неверные параметры");
        }
        if (query.isEmpty()) {
            log.warn("Отсутствует поисковый запрос");
            throw new EntityNotExistException("Отсутствует поисковый запрос");
        }
        return filmStorage.getSearchFilms(query, by);
    }
}
