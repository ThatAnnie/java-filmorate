package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController controller;

    @BeforeEach
    private void createController(){
        controller = new FilmController();
    }

    private Film createNewFilm(){
        Film film = new Film();
        film.setName("ТестНазвание");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }

    @Test
    void getFilms() {
        Film film1 = new Film();
        film1.setName("ТестНазвание");
        film1.setDescription("Описание");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1 = controller.saveFilm(film1);
        Film film2 = new Film();
        film2.setName("ТестНазвание2");
        film2.setDescription("Описание2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 2));
        film2.setDuration(200);
        film2 = controller.saveFilm(film2);
        List<Film> expectedFilms = new ArrayList<>();
        expectedFilms.add(film1);
        expectedFilms.add(film2);
        List<Film> films = controller.getFilms();
        assertEquals(expectedFilms, films);
    }

    @Test
    void saveFilm() {
        Film film = createNewFilm();
        Film filmInController = controller.saveFilm(film);
        assertEquals(1, controller.getFilms().size());
        assertEquals(film, filmInController);
    }

    @Test
    void updateFilm() {
        Film film = createNewFilm();
        controller.saveFilm(film);
        film.setDuration(300);
        Film filmInController = controller.updateFilm(film);
        assertEquals(film, filmInController);
    }

    @Test
    void updateNotExistedFilm() {
        Film film = createNewFilm();
        film.setId(9999);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.updateFilm(film));
        assertEquals("Фильм с таким id не существует.", ex.getMessage());
    }
}