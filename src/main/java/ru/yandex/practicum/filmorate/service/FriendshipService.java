package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class FriendshipService {
    private final FriendshipStorage friendshipStorage;
    private final UserStorage userStorage;
    private final EventService eventService;

    @Autowired
    public FriendshipService(FriendshipStorage friendshipStorage, UserStorage userStorage, EventService eventService) {
        this.friendshipStorage = friendshipStorage;
        this.userStorage = userStorage;
        this.eventService = eventService;
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
        friendshipStorage.addFriend(id, friendId);
        eventService.createEvent(id, EventType.FRIEND, Operation.ADD, friendId);
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
        if (!friendshipStorage.getFriends(id).contains(friendUser)) {
            log.warn("user with id={} has no friend with id={}", id, friendId);
            throw new EntityNotExistException(String.format("У пользователя с id=%d нет друга с id=%d.", id, friendId));
        }
        friendshipStorage.deleteFriend(id, friendId);
        eventService.createEvent(id, EventType.FRIEND, Operation.REMOVE, friendId);
    }

    public Collection<User> getFriends(Long id) {
        log.info("getFriends of user with id={}", id);
        User user = userStorage.getById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
        return friendshipStorage.getFriends(id);
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
        return friendshipStorage.getCommonFriends(id, otherId);
    }
}
