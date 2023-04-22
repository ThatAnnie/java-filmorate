package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("name"));
    }

    @Override
    public Genre save(Genre genre) {
        String sqlQuery = "INSERT INTO genres (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"genre_id"});
            stmt.setString(1, genre.getName());
            return stmt;
        }, keyHolder);
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        String sql = "UPDATE genres SET NAME = ? WHERE genre_id = ?";
        jdbcTemplate.update(sql, genre.getName(), genre.getId());
        return genre;
    }

    @Override
    public List<Genre> getList() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
        return genres;
    }

    @Override
    public Optional<Genre> getById(Long id) {
        final String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> result = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.get(0));
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        final String sql = "SELECT * FROM film_genre fg " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE film_id = ? ORDER BY g.genre_id";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
        if (genres.size() == 0) {
            return new ArrayList<>();
        }
        return genres;
    }

    @Override
    public void addFilmGenres(Film film) {
        final String sql = "MERGE INTO film_genre VALUES (?, ?)";
        film.getGenres().stream()
                .forEach((genre) -> {
                    jdbcTemplate.update(sql, film.getId(), genre.getId());
                });
    }

    @Override
    public void removeFilmGenres(Film film) {
        final String sql = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
        film.getGenres().stream()
                .forEach((genre) -> {
                    jdbcTemplate.update(sql, film.getId(), genre.getId());
                });
    }
}
