package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final Storage<User> userStorage;

    @Autowired
    public UserService(Storage<User> userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        log.info("getUsers");
        return userStorage.getList();
    }

    public User createUser(User user) {
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

        user.getFriends().add(friendId);
        friendUser.getFriends().add(id);
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
        if (!user.getFriends().contains(friendId)) {
            log.warn("user with id={} has no friend with id={}", id, friendId);
            throw new EntityNotExistException(String.format("У пользователя с id=%d нет друга с id=%d.", id, friendId));
        }

        user.getFriends().remove(friendId);
        friendUser.getFriends().remove(id);
    }

    public Collection<User> getFriends(Long id) {
        log.info("getFriends of user with id={}", id);
        User user = userStorage.getById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });

        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            return Collections.emptyList();
        } else {
            Set<Long> friendsSet = user.getFriends();
            ArrayList<User> friendsList = new ArrayList<>();
            for (Long friendId : friendsSet) {
                friendsList.add(userStorage.getById(friendId).get());
            }
            return friendsList;
        }
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
        Set<Long> common = new HashSet<>(user.getFriends());
        common.retainAll(otherUser.getFriends());
        if (common.isEmpty()) {
            return Collections.emptyList();
        } else {
            ArrayList<User> commonList = new ArrayList<>();
            for (Long commonId : common) {
                commonList.add(userStorage.getById(commonId).get());
            }
            return commonList;
        }
    }
}
