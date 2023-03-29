package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private InMemoryUserStorage userRepository= new InMemoryUserStorage();

    @GetMapping
    public List<User> getUsers() {
        log.info("getUsers");
        return userRepository.getUserList();
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        userRepository.saveUser(user);
        log.info("createUser: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.info("updateUser: {}", user);
        return userRepository.updateUser(user);
    }
}
