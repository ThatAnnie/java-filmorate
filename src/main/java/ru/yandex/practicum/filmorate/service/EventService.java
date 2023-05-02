package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.Collection;

@Service
@Slf4j
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Autowired
    public EventService(EventStorage eventStorage, UserStorage userStorage) {
        this.eventStorage = eventStorage;
        this.userStorage = userStorage;
    }

    public Collection<Event> getUserEvents(Long id) {
        log.info("getUserEvents by user with id={}", id);
        userStorage.getById(id).orElseThrow(() -> {
            log.warn("user with id={} not exist", id);
            throw new EntityNotExistException(String.format("Пользователь с id=%d не существует.", id));
        });
        return eventStorage.getUserEvents(id);
    }

    public void createEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        Event event = new Event();
        event.setTimestamp(Instant.now().toEpochMilli());
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);
        eventStorage.createEvent(event);
    }
}
