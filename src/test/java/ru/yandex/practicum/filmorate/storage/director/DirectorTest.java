package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate"})
@SqlGroup({
        @Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DirectorTest {

    @Autowired
    DirectorDbStorage directorDbStorage;

    @Autowired
    FilmDbStorage filmDbStorage;

    @Test
    public void test_getAll() {
        assertThat(directorDbStorage.getList().isEmpty()).isTrue();

        directorDbStorage.save(new Director(1L, "name"));

        assertThat(directorDbStorage.getList().isEmpty()).isFalse();
        assertThat(directorDbStorage.getList().size()).isEqualTo(1);
    }

    @Test
    public void test_save() {
        assertThat(directorDbStorage.getList().isEmpty()).isTrue();

        directorDbStorage.save(new Director(1L, "name"));

        assertThat(directorDbStorage.getList().isEmpty()).isFalse();
        assertThat(directorDbStorage.getList().size()).isEqualTo(1);

        assertThat(directorDbStorage.getList().get(0).getName()).isEqualTo("name");
    }

    @Test
    public void test_update() {

        directorDbStorage.save(new Director(1L, "name"));
        assertThat(directorDbStorage.getList().get(0).getName()).isEqualTo("name");

        directorDbStorage.update(new Director(1L, "updated"));
        assertThat(directorDbStorage.getList().get(0).getName()).isEqualTo("updated");
    }

    @Test
    public void test_getById() {

        directorDbStorage.save(new Director(1L, "name"));
        directorDbStorage.save(new Director(1L, "nameSecond"));

        assertThat(directorDbStorage.getById(2L).get().getName()).isEqualTo("nameSecond");
    }

    @Test
    public void test_deleteById() {

        directorDbStorage.save(new Director(1L, "name"));
        directorDbStorage.save(new Director(1L, "nameSecond"));

        assertThat(directorDbStorage.getById(2L).get().getName()).isEqualTo("nameSecond");
        assertThat(directorDbStorage.getList().isEmpty()).isFalse();
        assertThat(directorDbStorage.getList().size()).isEqualTo(2);

        directorDbStorage.delete(2L);
        assertThat(directorDbStorage.getList().isEmpty()).isFalse();
        assertThat(directorDbStorage.getList().size()).isEqualTo(1);
        assertThat(directorDbStorage.getList().get(0).getName()).isEqualTo("name");

    }

    @Test
    public void test_getDirectorsByFilmId() {
        Director director = new Director(1L, "NAME");
        directorDbStorage.save(director);
        HashSet<Director> directors = new HashSet<>();
        directors.add(director);
        Rating mpa = new Rating(1, "G");
        Film film = new Film();
        film.setName("FilmName");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 02, 9));
        film.setMpa(mpa);
        film.setDirectors(directors);
        Film filmDB = filmDbStorage.save(film);

        assertThat(directorDbStorage.getDirectorsByFilmId(1L).size()).isEqualTo(1);
        assertThat(directorDbStorage.getDirectorsByFilmId(1L).isEmpty()).isFalse();
    }

    @Test
    public void addFilmDirectors() {
        Director director = new Director(1L, "NAME");
        directorDbStorage.save(director);
        HashSet<Director> directors = new HashSet<>();
        directors.add(director);
        Director directorTwo = new Director(2L, "NAME222");
        directorDbStorage.save(directorTwo);
        directors.add(directorTwo);
        Rating mpa = new Rating(1, "G");
        Film film = new Film();
        film.setName("FilmName");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 02, 9));
        film.setMpa(mpa);
        film.setDirectors(directors);
        Film filmDB = filmDbStorage.save(film);

        assertThat(directorDbStorage.getDirectorsByFilmId(1L).size()).isEqualTo(2);
        assertThat(directorDbStorage.getDirectorsByFilmId(1L).isEmpty()).isFalse();

    }

    @Test
    public void test_deleteDirectorsFromFilm() {
        Director director = new Director(1L, "NAME");
        directorDbStorage.save(director);
        HashSet<Director> directors = new HashSet<>();
        directors.add(director);

        Rating mpa = new Rating(1, "G");
        Film film = new Film();
        film.setName("FilmName");
        film.setDescription("Description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 02, 9));
        film.setMpa(mpa);
        film.setDirectors(directors);
        Film filmDB = filmDbStorage.save(film);

        assertThat(directorDbStorage.getDirectorsByFilmId(1L).size()).isEqualTo(1);
        assertThat(directorDbStorage.getDirectorsByFilmId(1L).isEmpty()).isFalse();

        directorDbStorage.deleteDirectorsFromFilm(1L, directors);

        assertThat(directorDbStorage.getDirectorsByFilmId(1L).isEmpty()).isTrue();
    }

}