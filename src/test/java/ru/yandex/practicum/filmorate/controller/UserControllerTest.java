package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController controller;

    @BeforeEach
    private void createController(){
        controller = new UserController();
    }

    private User createNewUser() {
        User user = new User();
        user.setLogin("test");
        user.setEmail("test@test.ru");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        return user;
    }

    @Test
    void getUsers() {
        User user1 = new User();
        user1.setLogin("test");
        user1.setEmail("test@test.ru");
        user1.setBirthday(LocalDate.of(1999, 1, 1));
        controller.createUser(user1);
        User user2 = new User();
        user2.setLogin("test2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        controller.createUser(user2);
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user2);
        List<User> users = controller.getUsers();
        assertEquals(expectedUsers, users);
    }

    @Test
    void createUser() {
        User user = createNewUser();
        User userInController = controller.createUser(user);
        assertEquals(1, controller.getUsers().size());
        assertEquals(user, userInController);
    }

    @Test
    void updateUser() {
        User user = createNewUser();
        controller.createUser(user);
        user.setName("Новое имя");
        User userInController = controller.updateUser(user);
        assertEquals(user, userInController);
    }

    @Test
    void updateNotExistedFilm() {
        User user = createNewUser();
        user.setId(9999);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.updateUser(user));
        assertEquals("Пользователь с таким id не существует.", ex.getMessage());
    }
}