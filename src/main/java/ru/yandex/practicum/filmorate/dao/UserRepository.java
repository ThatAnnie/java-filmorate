package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Slf4j
public class UserRepository {
    private long generatedUserId;
    private final HashMap<Long, User> users = new HashMap<>();

    private long generateId() {
        return ++generatedUserId;
    }

    public User saveUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("user with id={} not exist", user.getId());
            throw new EntityNotExistException("Пользователь с таким id не существует.");
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public ArrayList<User> getUserList() {
        return new ArrayList<>(users.values());
    }
}
