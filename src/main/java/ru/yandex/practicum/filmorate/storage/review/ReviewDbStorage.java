package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private Review makeReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("review_id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUserId(rs.getLong("user_id"));
        review.setFilmId(rs.getLong("film_id"));
        review.setUseful(rs.getLong("useful"));
        return review;
    }

    @Override
    public Review save(Review review) {
        if (userStorage.getById(review.getUserId()).isEmpty()) {
            log.warn("user with id={} not exist", review.getUserId());
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", review.getUserId()));
        }
        if (filmStorage.getById(review.getFilmId()).isEmpty()) {
            log.warn("film with id={} not exist", review.getFilmId());
            throw new EntityNotExistException(String.format("Фильм с id=%d не существует.", review.getFilmId()));
        }

        String sqlQuery = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId((Long) keyHolder.getKey());

        return review;
    }

    @Override
    public Review update(Review review) {
        getById(review.getReviewId()).orElseThrow(() -> {
            log.warn("review with id={} not exist", review.getReviewId());
            throw new EntityNotExistException(String.format("Отзыва с id=%d не существует.", review.getReviewId()));
        });

        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        return getById(review.getReviewId()).get();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Review> getById(Long id) {
        final String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> result = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), id);
        if (result.isEmpty()) {
            throw new EntityNotExistException(String.format("Ревью с id=%d не существует.", id));
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<Review> getList() {
        String sql = "SELECT * FROM reviews ORDER BY useful";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public List<Review> getListWithParam(Long filmId, Integer count) {
        if (filmId == null) {
            String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), count);
        } else {
            String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count);
        }
    }
}
