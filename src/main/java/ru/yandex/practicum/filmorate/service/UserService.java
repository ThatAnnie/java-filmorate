package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserService {
    private final UserStorage userStorage;

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

    public void deleteUser(Long userId) {
        log.info("deleteUser with id={}", userId);
        userStorage.getById(userId).orElseThrow(() -> {
            log.warn("user with id={} not exist", userId);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", userId));
        });
        userStorage.delete(userId);
    }
}
