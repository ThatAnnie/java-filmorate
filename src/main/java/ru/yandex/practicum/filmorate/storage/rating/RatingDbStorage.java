package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@Primary
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        return new Rating(rs.getInt("rating_id"), rs.getString("name"));
    }

    @Override
    public Rating save(Rating rating) {
        String sqlQuery = "INSERT INTO rating (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"rating_id"});
            stmt.setString(1, rating.getName());
            return stmt;
        }, keyHolder);
        return rating;
    }

    @Override
    public Rating update(Rating rating) {
        String sql = "UPDATE rating SET NAME = ? WHERE rating_id = ?";
        jdbcTemplate.update(sql, rating.getName(), rating.getId());
        return rating;
    }

    @Override
    public List<Rating> getList() {
        String sql = "SELECT * FROM rating ORDER BY rating_id";
        List<Rating> ratings = jdbcTemplate.query(sql, (rs, rowNum) -> makeRating(rs));
        return ratings;
    }

    @Override
    public Optional<Rating> getById(Long id) {
        final String sql = "SELECT * FROM rating WHERE rating_id = ?";
        List<Rating> result = jdbcTemplate.query(sql, (rs, rowNum) -> makeRating(rs), id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.get(0));
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM rating WHERE rating_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
