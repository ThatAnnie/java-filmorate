package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.Collection;
import java.util.List;

public interface FilmStorage extends Storage<Film> {
    Collection<Film> getFilmsByDirId(Long dirId);

    List<Film> getSearchFilms(String query, List<String> by);
}
