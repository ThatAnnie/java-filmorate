package ru.yandex.practicum.filmorate.storage.search;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate"})
@SqlGroup({
        @Sql(scripts = "/test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SearchTest {

    @Autowired
    FilmDbStorage filmDbStorage;

    @Test
    void testGetSearchFilms() {

        String query = "илЬ";
        List<String> by = new ArrayList<>();
        by.add("title");
        by.add("director");
        Collection<Film> getSearchFilms = filmDbStorage.getSearchFilms(query, by);
        System.out.println(getSearchFilms);
        assertThat(getSearchFilms.size()).isEqualTo(2);

        by.clear();
        by.add("director");
        Collection<Film> getSearchFilms1 = filmDbStorage.getSearchFilms(query, by);
        assertThat(getSearchFilms1.size()).isEqualTo(1);

        by.clear();
        by.add("title");
        Collection<Film> getSearchFilms2 = filmDbStorage.getSearchFilms(query, by);
        assertThat(getSearchFilms2.size()).isEqualTo(1);
    }
}