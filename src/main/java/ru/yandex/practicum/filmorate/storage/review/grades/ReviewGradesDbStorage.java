package ru.yandex.practicum.filmorate.storage.review.grades;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ReviewGradesDbStorage implements ReviewGradesStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;

    private void isExist(long id, long userId) {
        if (userStorage.getById(userId).isEmpty()) {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        }
        if (reviewStorage.getById(id).isEmpty()) {
            log.warn("review with id={} not exist", id);
            throw new EntityNotExistException(String.format("Отзыв с id=%d не существует.", userId));
        }
    }

    private void updateUseful(long id, long estimation) {
        String sqlGetUseful = "SELECT useful FROM reviews WHERE review_id = ?";
        long curUseful = jdbcTemplate.query(sqlGetUseful, (rs, rowNum) -> rs.getLong("useful"), id).get(0);
        String sql = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, curUseful + estimation, id);
    }

    @Override
    public void addLike(long id, long userId) {
        isExist(id, userId);
        String sql = "INSERT INTO review_grades (review_id, user_id, is_useful) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, true);
        updateUseful(id, 1);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        isExist(id, userId);
        String sql = "DELETE FROM review_grades WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        updateUseful(id, -1);
    }

    @Override
    public void addDislike(long id, long userId) {
        isExist(id, userId);
        String sql = "INSERT INTO review_grades (review_id, user_id, is_useful) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, false);
        updateUseful(id, -1);
    }

    @Override
    public void deleteDislike(Long id, Long userId) {
        isExist(id, userId);
        String sql = "DELETE FROM review_grades WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        updateUseful(id, 1);
    }
}
