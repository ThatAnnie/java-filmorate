package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Slf4j
public class FilmRepository {
    private long generatedFilmId;
    private final HashMap<Long, Film> films = new HashMap<>();

    private long generateId() {
        return ++generatedFilmId;
    }

    public Film saveFilm(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("film with id={} not exist", film.getId());
            throw new EntityNotExistException("Фильм с таким id не существует.");
        }
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    public ArrayList<Film> getFilmList() {
        return new ArrayList<>(films.values());
    }
}
