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
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        log.info("getUsers");
        return userStorage.getUserList();
    }

    public User createUser(User user) {
        userStorage.saveUser(user);
        log.info("createUser: {}", user);
        return user;
    }

    public User updateUser(User user) {
        log.info("updateUser: {}", user);
        return userStorage.updateUser(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
    }

    public void addFriend(Long id, Long friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
        User friendUser = userStorage.getUserById(friendId).orElseThrow(() -> {
            log.warn("user with id={} not exist", friendId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", friendId));
        });

        user.getFriends().add(friendId);
        friendUser.getFriends().add(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
        User friendUser = userStorage.getUserById(friendId).orElseThrow(() -> {
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
        User user = userStorage.getUserById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });

        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            return Collections.emptyList();
        } else {
            Set<Long> friendsSet = user.getFriends();
            ArrayList<User> friendsList= new ArrayList<>();
            for (Long friendId: friendsSet) {
                friendsList.add(userStorage.getUserById(friendId).get());
            }
            return friendsList;
        }
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
        User otherUser = userStorage.getUserById(otherId).orElseThrow(() -> {
            log.warn("user with id={} not exist", otherId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", otherId));
        });
        Set<Long> common = new HashSet<>(user.getFriends());
        common.retainAll(otherUser.getFriends());
        if (common == null || common.isEmpty()) {
            return Collections.emptyList();
        } else {
            ArrayList<User> commonList= new ArrayList<>();
            for (Long commonId: common) {
                commonList.add(userStorage.getUserById(commonId).get());
            }
            return commonList;
        }
    }
}
