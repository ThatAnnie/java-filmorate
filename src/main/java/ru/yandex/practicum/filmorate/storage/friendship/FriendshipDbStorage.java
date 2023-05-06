package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

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
        result.forEach((friendId) -> friends.add(userDbStorage.getById(friendId).get()));
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sql = "SELECT f1.friend_id FROM friendship f1 INNER JOIN friendship f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        List<Long> result = jdbcTemplate.queryForList(sql, Long.class, id, otherId);
        List<User> commonFriends = new ArrayList<>();
        result.forEach((friendId) -> commonFriends.add(userDbStorage.getById(friendId).get()));
        return commonFriends;
    }
}
