package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventStorage {
    void create(Event event);

    Collection<Event> getEventsByUserId(Long id);
}
