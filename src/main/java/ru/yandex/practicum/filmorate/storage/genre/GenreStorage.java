package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface GenreStorage extends Storage<Genre> {
    List<Genre> getGenresByFilmId(Long filmId);

    void addFilmGenres(Film film);

    void removeFilmGenres(Film film);
}
