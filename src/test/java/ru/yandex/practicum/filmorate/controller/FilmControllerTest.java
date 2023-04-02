package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
private FilmController controller;
private UserStorage userStorage;

    @BeforeEach
    private void createController(){
        FilmStorage filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage) ;
        controller = new FilmController(filmService);
    }

    private Film createNewFilm(){
        Film film = new Film();
        film.setName("ТестНазвание");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }

    private User createNewUser() {
        User user = new User();
        user.setLogin("test");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        userStorage.saveUser(user);
        return user;
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

    @Test
    void findFilm() {
        Film film = createNewFilm();
        controller.saveFilm(film);
        Film filmInController = controller.findFilm(1L);
        assertEquals(film, filmInController);
    }

    @Test
    void findNotExistFilm() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.findFilm(1L));
        assertEquals("Фильм с id=1 не существует.", ex.getMessage());
    }

    @Test
    void addLike() {
        Film film = createNewFilm();
        controller.saveFilm(film);
        createNewUser();
        controller.addLike(film.getId(), 1L);
        assertTrue(film.getUsersLikes().contains(1L));
        controller.addLike(film.getId(), 1L);
        assertEquals(1, film.getUsersLikes().size());
    }

    @Test
    void addLikeFilmNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.addLike(1L, 1L));
        assertEquals("Фильм с id=1 не существует.", ex.getMessage());
    }

    @Test
    void addLikeUserNotExist() {
        Film film = createNewFilm();
        controller.saveFilm(film);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.addLike(1L, 1L));
        assertEquals("Пользователь с id=1 не существует.", ex.getMessage());
    }

    @Test
    void deleteLike() {
        Film film = createNewFilm();
        controller.saveFilm(film);
        createNewUser();
        controller.addLike(film.getId(), 1L);
        assertTrue(film.getUsersLikes().contains(1L));
        controller.deleteLike(film.getId(), 1L);
        assertFalse(film.getUsersLikes().contains(1L));
    }

    @Test
    void deleteLikeFilmNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.deleteLike(1L, 1L));
        assertEquals("Фильм с id=1 не существует.", ex.getMessage());
    }

    @Test
    void deleteLikeUserNotExist() {
        Film film = createNewFilm();
        controller.saveFilm(film);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.deleteLike(1L, 1L));
        assertEquals("Пользователь с id=1 не существует.", ex.getMessage());
    }

    @Test
    void findPopularFilms() {
        User user1 = createNewUser();
        User user2 = createNewUser();
        createNewUser();
        createNewUser();
        Film film1 = createNewFilm();
        controller.saveFilm(film1);
        controller.addLike(film1.getId(), user1.getId());
        Film film2 = new Film();
        film2.setName("ТестНазвание2");
        film2.setDescription("Описание2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 2));
        film2.setDuration(200);
        controller.saveFilm(film2);
        controller.addLike(film2.getId(), user1.getId());
        controller.addLike(film2.getId(), user2.getId());
        assertEquals(1, controller.findPopularFilms(1).size());
        assertTrue(controller.findPopularFilms(1).contains(film2));
    }
}