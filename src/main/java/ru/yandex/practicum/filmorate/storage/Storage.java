package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {
    T save(T entity);

    T update(T entity);

    List<T> getList();

    Optional<T> getById(Long id);
}
