package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        log.info("getUsers");
        return userStorage.getList();
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        userStorage.save(user);
        log.info("createUser: {}", user);
        return user;
    }

    public User updateUser(User user) {
        log.info("updateUser: {}", user);
        return userStorage.update(user);
    }

    public User getUserById(Long id) {
        log.info("getUserById with id={}", id);
        return userStorage.getById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
    }

    public void addFriend(Long id, Long friendId) {
        log.info("addFriend - user with id={} add friend with id={}", id, friendId);
        User user = userStorage.getById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
        User friendUser = userStorage.getById(friendId).orElseThrow(() -> {
            log.warn("user with id={} not exist", friendId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", friendId));
        });
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        log.info("deleteFriend - user with id={} delete friend with id={}", id, friendId);
        User user = userStorage.getById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
        User friendUser = userStorage.getById(friendId).orElseThrow(() -> {
            log.warn("user with id={} not exist", friendId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", friendId));
        });
        if (!userStorage.getFriends(id).contains(friendUser)) {
            log.warn("user with id={} has no friend with id={}", id, friendId);
            throw new EntityNotExistException(String.format("У пользователя с id=%d нет друга с id=%d.", id, friendId));
        }
        userStorage.deleteFriend(id, friendId);
        user.getFriends().remove(friendId);
        friendUser.getFriends().remove(id);
    }

    public Collection<User> getFriends(Long id) {
        log.info("getFriends of user with id={}", id);
        User user = userStorage.getById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        log.info("getCommonFriends of users with id={} and id={}", id, otherId);
        User user = userStorage.getById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
        User otherUser = userStorage.getById(otherId).orElseThrow(() -> {
            log.warn("user with id={} not exist", otherId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", otherId));
        });
        return userStorage.getCommonFriends(id, otherId);
    }
}
