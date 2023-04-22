package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreStorage;
    private final RatingDbStorage ratingStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreStorage, RatingDbStorage ratingStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Rating rating = ratingStorage.getById(rs.getLong("rating_id")).get();
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setMpa(rating);
        return film;
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "INSERT INTO films (name,  description, duration, release_date, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setLong(3, film.getDuration());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        film.setMpa(ratingStorage.getById(film.getMpa().getId()).orElseThrow(() -> {
            log.warn("mpa with id={} not exist", film.getMpa().getId());
            throw new EntityNotExistException(String.format("MPA с id=%d не существует.", film.getMpa().getId()));
        }));
        if (!film.getGenres().isEmpty() && film.getGenres() != null) {
            genreStorage.addFilmGenres(film);
            film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        Film filmDB = getById(film.getId()).orElseThrow(() -> {
            log.warn("film with id={} not exist", film.getId());
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует.", film.getId()));
        });
        if (!filmDB.getGenres().isEmpty()) {
            genreStorage.removeFilmGenres(filmDB);
            log.warn("removeFilmGenres");
        }
        String sql = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate(),
                film.getMpa().getId(),
                film.getId());
        if (!film.getGenres().isEmpty() && film.getGenres() != null) {
            genreStorage.addFilmGenres(film);
            film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
        }
        film.setMpa(ratingStorage.getById(film.getMpa().getId()).orElseThrow(() -> {
            log.warn("mpa with id={} not exist", film.getMpa().getId());
            throw new EntityNotExistException(String.format("MPA с id=%d не существует.", film.getMpa().getId()));
        }));
        return film;
    }

    @Override
    public List<Film> getList() {
        String sql = "SELECT * FROM films ORDER BY film_id";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        films.stream().forEach((film) -> film.setGenres(genreStorage.getGenresByFilmId(film.getId())));
        return films;
    }

    @Override
    public Optional<Film> getById(Long id) {
        final String sql = "SELECT * FROM films WHERE film_id = ?";
        List<Film> result = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        Film film = result.get(0);
        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
        return Optional.ofNullable(film);
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
        result.forEach((filmId) -> popularFilms.add(getById(filmId).get()));
        return popularFilms;
    }
}