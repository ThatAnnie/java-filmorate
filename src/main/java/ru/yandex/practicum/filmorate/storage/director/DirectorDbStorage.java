package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class DirectorDbStorage implements DirectorStorage {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Director mapRowForDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("director_id"))
                .name(resultSet.getString("dir_name"))
                .build();
    }

    @Override
    public Director save(Director entity) {
        String sqlQuery = "INSERT INTO directors (dir_name) values (?)";
        jdbcTemplate.update(sqlQuery, entity.getName());
        String sqlQueryTwo = "SELECT * FROM directors WHERE dir_name = ?";
        return jdbcTemplate.queryForObject(sqlQueryTwo, this::mapRowForDirector, entity.getName());
    }

    @Override
    public Director update(Director entity) throws EmptyResultDataAccessException {
        String sqlQuery = "update directors set dir_name = ? WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, entity.getName(), entity.getId());
        String sqlQueryTwo = "SELECT * FROM directors WHERE director_id = ?";
        return jdbcTemplate.queryForObject(sqlQueryTwo, this::mapRowForDirector, entity.getId());
    }

    @Override
    public List<Director> getList() {
        String sqlQuery = "SELECT * FROM directors";
        return jdbcTemplate.query(sqlQuery, this::mapRowForDirector);
    }

    @Override
    public Optional<Director> getById(Long id) {
        try {
            String sqlQuery = "SELECT * FROM directors WHERE director_id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowForDirector, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM film_director WHERE director_id = ?";
        jdbcTemplate.update(query, id);
        String sqlQuery = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public Set<Director> getDirectorsByFilmId(Long id) {
        try {
            String sqlQuery = "SELECT * FROM directors d " +
                    "JOIN FILM_DIRECTOR fd ON d.DIRECTOR_ID = FD.DIRECTOR_ID " +
                    "WHERE fd.FILM_ID = ?";
            return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowForDirector, id));
        } catch (EmptyResultDataAccessException e) {
            return new HashSet<>();
        }
    }

    public void addFilmDirectors(Set<Director> directors, long id) {
        String sqlQuery = "INSERT INTO film_director (film_id, director_id) values (?,?)";
        for (Director d : directors) {
            jdbcTemplate.update(sqlQuery, id, d.getId());
        }
    }

    public void deleteDirectorsFromFilm(long filmId, Set<Director> directors) {
        String sqlQuery = "DELETE FROM film_director WHERE film_id = ? AND director_id = ?";
        for (Director d : directors) {
            jdbcTemplate.update(sqlQuery, filmId, d.getId());
        }
    }
}
