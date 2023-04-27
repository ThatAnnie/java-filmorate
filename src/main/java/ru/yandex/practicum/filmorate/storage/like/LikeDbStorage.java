package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public void addLike(Long id, Long userId) {
        String sql = "INSERT INTO film_like (film_id, user_id) VALUES(?, ?)";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        final String sql = "SELECT f.film_id FROM films f " +
                "LEFT JOIN film_like fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        List<Long> result = jdbcTemplate.queryForList(sql, Long.class, count);
        List<Film> popularFilms = new ArrayList<>();
        result.forEach((filmId) -> popularFilms.add(filmDbStorage.getById(filmId).get()));
        return popularFilms;
    }

    @Override
    public Collection<Long> getUsersLikesByFilm(Long userId) {
        final String sql = "SELECT user_id FROM film_like WHERE film_id = ?";
        List<Long> users = jdbcTemplate.queryForList(sql, Long.class, userId);
        return users;
    }

    @Override
    public Collection<Film> getSortedFilmByLikesDirector(Long dirId) {
        String sqlQuery ="SELECT f.film_id FROM films f " +
                "LEFT JOIN FILM_DIRECTOR fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN film_like fl ON fd.film_id = fl.film_id " +
                "WHERE fd.DIRECTOR_ID = ? " +
                "GROUP BY f.FILM_ID  ORDER BY COUNT(director_id) DESC";
        List<Long> result = jdbcTemplate.queryForList(sqlQuery, Long.class, dirId);
        List<Film> dirFilmsSortedByLikes = new ArrayList<>();
        result.forEach((filmId) -> dirFilmsSortedByLikes.add(filmDbStorage.getById(filmId).get()));
        return dirFilmsSortedByLikes;
    }
}
