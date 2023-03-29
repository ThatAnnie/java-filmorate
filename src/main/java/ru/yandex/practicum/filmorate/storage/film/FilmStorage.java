package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

public interface FilmStorage {
    Film saveFilm(Film film);
    Film updateFilm(Film film);
    ArrayList<Film> getFilmList();
}
