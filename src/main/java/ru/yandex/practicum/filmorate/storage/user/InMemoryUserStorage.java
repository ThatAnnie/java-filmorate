package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long generatedUserId;
    private final HashMap<Long, User> users = new HashMap<>();

    private long generateId() {
        return ++generatedUserId;
    }

    @Override
    public User save(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("user with id={} not exist", user.getId());
            throw new EntityNotExistException("Пользователь с таким id не существует.");
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public List<User> getList() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        User user = getById(id).get();
        user.getFriends().add(friendId);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        User user = getById(id).get();
        user.getFriends().remove(friendId);
        User friendUser = getById(friendId).get();
        friendUser.getFriends().remove(id);
    }

    @Override
    public List<User> getFriends(Long id) {
        Set<Long> friendsSet = getById(id).get().getFriends();
        ArrayList<User> friendsList = new ArrayList<>();
        for (Long friendId : friendsSet) {
            friendsList.add(getById(friendId).get());
        }
        return friendsList;
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        User user = getById(id).get();
        User otherUser = getById(otherId).get();
        Set<Long> common = new HashSet<>(user.getFriends());
        common.retainAll(otherUser.getFriends());
        if (common.isEmpty()) {
            return Collections.emptyList();
        } else {
            ArrayList<User> commonList = new ArrayList<>();
            for (Long commonId : common) {
                commonList.add(getById(commonId).get());
            }
            return commonList;
        }
    }
}
