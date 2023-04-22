package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    private boolean isUserExist(Long id) {
        final String sql = "SELECT name FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, id).size() > 0;
    }

    @Override
    public User save(User user) {
        final String sqlQuery = "INSERT INTO users (name, email, login, birthday) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        if (!isUserExist(user.getId())) {
            log.warn("user with id={} not exist", user.getId());
            throw new EntityNotExistException("Пользователь с таким id не существует.");
        }
        String sql = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public List<User> getList() {
        final String sql = "SELECT * FROM users ORDER BY user_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> getById(Long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> result = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(result.get(0));
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        String sql = "INSERT INTO friendship (user_id, friend_id) VALUES(?, ?)";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        String sql = "SELECT friend_id FROM friendship WHERE user_id = ? order by friend_id";

        List<Long> result = jdbcTemplate.queryForList(sql, Long.class, id);
        List<User> friends = new ArrayList<>();
        result.forEach((friendId) -> friends.add(getById(friendId).get()));
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sql = "SELECT f1.friend_id FROM friendship f1 INNER JOIN friendship f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        List<Long> result = jdbcTemplate.queryForList(sql, Long.class, id, otherId);
        List<User> commonFriends = new ArrayList<>();
        result.forEach((friendId) -> commonFriends.add(getById(friendId).get()));
        return commonFriends;
    }
}
