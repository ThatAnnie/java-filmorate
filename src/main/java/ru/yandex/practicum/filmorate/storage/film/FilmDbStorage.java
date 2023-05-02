package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreStorage;
    private final RatingDbStorage ratingStorage;
    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreStorage, RatingDbStorage ratingStorage, DirectorDbStorage directorDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.directorDbStorage = directorDbStorage;
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
        film.setDirectors(directorDbStorage.getDirectorsByFilmId(rs.getLong("film_id")));
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
            Set<Genre> filmGenres = new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId()));
            film.setGenres(filmGenres);
        }
        if (!film.getDirectors().isEmpty() && film.getDirectors() != null) {
            directorDbStorage.addFilmDirectors(film.getDirectors(), film.getId());
            Set<Director> filmDirectors = new LinkedHashSet<>(directorDbStorage.getDirectorsByFilmId(film.getId()));
            film.setDirectors(filmDirectors);
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
        if (!filmDB.getDirectors().isEmpty()) {
            directorDbStorage.deleteDirectorsFromFilm(filmDB.getId());
            log.warn("removeFilmDirectors");
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
            Set<Genre> filmGenres = new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId()));
            film.setGenres(filmGenres);
        }
        if (!film.getDirectors().isEmpty() && film.getDirectors() != null) {
            directorDbStorage.addFilmDirectors(film.getDirectors(), film.getId());
            Set<Director> filmDir = directorDbStorage.getDirectorsByFilmId(film.getId());
            film.setDirectors(filmDir);
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
        films.stream().forEach((film) -> film.setGenres(new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId()))));
        films.stream().forEach((film) -> film.setDirectors(directorDbStorage.getDirectorsByFilmId(film.getId())));
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
        Set<Genre> filmGenres = new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId()));
        film.setGenres(filmGenres);
        Set<Director> directors = directorDbStorage.getDirectorsByFilmId(id);
        film.setDirectors(directors);
        return Optional.ofNullable(film);
    }

    @Override
    public Collection<Film> getFilmsByDirId(Long dirId) {
        String sqlQuery = "SELECT * " +
                "FROM FILMS f " +
                "LEFT JOIN FILM_DIRECTOR fd2 ON f.FILM_ID = fd2.FILM_ID " +
                "WHERE FD2.DIRECTOR_ID = ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), dirId);
        films.stream().forEach((film) -> film.setGenres(new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId()))));
        films.stream().forEach((film) -> film.setDirectors(directorDbStorage.getDirectorsByFilmId(film.getId())));
        return films;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getSearchFilms(String query, List<String> by) {
        if (by.size() == 2) {
            String sql = "SELECT f.*, COUNT(user_id) " +
                    "FROM films f LEFT JOIN film_like fl ON f.film_id = fl.film_id " +
                    "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN directors d ON fd.director_id = d.director_id " +
                    "WHERE lower(d.dir_name) LIKE lower(concat('%',?,'%')) OR lower(f.name) LIKE lower(concat('%',?,'%')) " +
                    "GROUP BY f.film_id ORDER BY COUNT(user_id) DESC";
            List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), query, query);
            films.stream().forEach((film) -> film.setGenres(new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId()))));
            return films;
        }
        if (by.contains("title")) {
            String sql = "SELECT f.film_id, f.name, f.description, f.duration, f.release_date, f.rating_id, COUNT(user_id) " +
                    "FROM films f LEFT JOIN film_like fl ON f.film_id = fl.film_id " +
                    "WHERE lower(name) LIKE lower(concat('%',?,'%')) GROUP BY f.film_id ORDER BY COUNT(user_id) DESC";
            List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), query);
            films.stream().forEach((film) -> film.setGenres(new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId()))));
            return films;
        }
        String sql = "SELECT f.*, COUNT(user_id) " +
                "FROM films f LEFT JOIN film_like fl ON f.film_id = fl.film_id " +
                "LEFT JOIN film_director fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.director_id " +
                "WHERE lower(d.dir_name) LIKE lower(concat('%',?,'%')) GROUP BY f.film_id ORDER BY COUNT(user_id) DESC";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), query);
        films.stream().forEach((film) -> film.setGenres(new LinkedHashSet<>(genreStorage.getGenresByFilmId(film.getId()))));
        return films;
    }
}
