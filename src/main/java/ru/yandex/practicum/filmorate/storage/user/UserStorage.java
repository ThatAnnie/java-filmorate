package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Optional;

public interface UserStorage {
    User saveUser(User user);
    User updateUser(User user);
    ArrayList<User> getUserList();
    Optional<User> getUserById(Long id);
}
