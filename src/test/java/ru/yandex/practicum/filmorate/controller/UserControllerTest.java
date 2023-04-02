package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserControllerTest {

    private UserController controller;

    @BeforeEach
    private void createController(){
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        controller = new UserController(userService);
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

    @Test
    void findUser(){
        User user = createNewUser();
        controller.createUser(user);
        User userInController = controller.findUser(1L);
        assertEquals(user, userInController);
    }

    @Test
    void findNotExistUser(){
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.findUser(1L));
        assertEquals("Пользователь с id=1 не существует.", ex.getMessage());
    }

    @Test
    void addFriend() {
        User user1 = createNewUser();
        controller.createUser(user1);
        User user2 = new User();
        user2.setLogin("test2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        controller.createUser(user2);
        controller.addFriend(user1.getId(), user2.getId());
        assertTrue(user2.getFriends().contains(user1.getId()));
        assertTrue(user1.getFriends().contains(user2.getId()));
    }

    @Test
    void addFriendUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.addFriend(1L, 2L));
        assertEquals("Пользователь с id=1 не существует.", ex.getMessage());
    }

    @Test
    void addFriendUserFriendNotExist() {
        User user1 = createNewUser();
        controller.createUser(user1);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.addFriend(1L, 2L));
        assertEquals("Пользователь с id=2 не существует.", ex.getMessage());
    }

    @Test
    void deleteFriend() {
        User user1 = createNewUser();
        controller.createUser(user1);
        User user2 = new User();
        user2.setLogin("test2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        controller.createUser(user2);
        controller.addFriend(user1.getId(), user2.getId());
        controller.deleteFriend(user1.getId(), user2.getId());
        assertFalse(user2.getFriends().contains(user1.getId()));
        assertFalse(user1.getFriends().contains(user2.getId()));
    }

    @Test
    void deleteFriendUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.deleteFriend(1L, 2L));
        assertEquals("Пользователь с id=1 не существует.", ex.getMessage());
    }

    @Test
    void deleteFriendUserFriendNotExist() {
        User user1 = createNewUser();
        controller.createUser(user1);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.deleteFriend(1L, 2L));
        assertEquals("Пользователь с id=2 не существует.", ex.getMessage());
    }

    @Test
    void deleteFriendUserHasNoFriend() {
        User user1 = createNewUser();
        controller.createUser(user1);
        User user2 = new User();
        user2.setLogin("test2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        controller.createUser(user2);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.deleteFriend(1L, 2L));
        assertEquals("У пользователя с id=1 нет друга с id=2.", ex.getMessage());
    }

    @Test
    void findFriends() {
        User user1 = createNewUser();
        controller.createUser(user1);
        User user2 = new User();
        user2.setLogin("test2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        user2 = controller.createUser(user2);
        User user3 = new User();
        user3.setLogin("test3");
        user3.setEmail("test3@test.ru");
        user3.setBirthday(LocalDate.of(2003, 1, 1));
        user3 = controller.createUser(user3);
        controller.addFriend(user1.getId(), user2.getId());
        controller.addFriend(user1.getId(), user3.getId());
        assertEquals(2, controller.findFriends(user1.getId()).size());
        assertTrue(controller.findFriends(user1.getId()).contains(user2));
        assertTrue(controller.findFriends(user1.getId()).contains(user3));
    }

    @Test
    void findFriendsUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.findFriends(1L));
        assertEquals("Пользователь с id=1 не существует.", ex.getMessage());
    }

    @Test
    void findFriendsUserHasNoFriends() {
        User user1 = createNewUser();
        controller.createUser(user1);
        assertEquals(0, controller.findFriends(1L).size());
    }

    @Test
    void findCommonFriends() {
        User user1 = createNewUser();
        controller.createUser(user1);
        User user2 = new User();
        user2.setLogin("test2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        user2 = controller.createUser(user2);
        User user3 = new User();
        user3.setLogin("test3");
        user3.setEmail("test3@test.ru");
        user3.setBirthday(LocalDate.of(2003, 1, 1));
        user3 = controller.createUser(user3);
        controller.addFriend(user1.getId(), user2.getId());
        controller.addFriend(user3.getId(), user2.getId());
        assertEquals(1, controller.findCommonFriends(user1.getId(), user3.getId()).size());
        assertTrue(controller.findCommonFriends(user1.getId(), user3.getId()).contains(user2));
    }

    @Test
    void findCommonFriendsUserNotExist() {
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.findCommonFriends(1L, 2L));
        assertEquals("Пользователь с id=1 не существует.", ex.getMessage());
    }

    @Test
    void findCommonFriendsUserFriendNotExist() {
        User user1 = createNewUser();
        controller.createUser(user1);
        EntityNotExistException ex = assertThrows(EntityNotExistException.class, () -> controller.findCommonFriends(1L, 2L));
        assertEquals("Пользователь с id=2 не существует.", ex.getMessage());
    }

    @Test
    void indCommonFriendsNoCommonFriends() {
        User user1 = createNewUser();
        controller.createUser(user1);
        User user2 = new User();
        user2.setLogin("test2");
        user2.setEmail("test2@test.ru");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        controller.createUser(user2);
        assertEquals(0, controller.findCommonFriends(1L, 2L).size());
    }

}