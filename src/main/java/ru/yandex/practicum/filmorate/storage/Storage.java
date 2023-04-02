package ru.yandex.practicum.filmorate.storage;

import java.util.ArrayList;
import java.util.Optional;

public interface Storage<T> {
    T save(T entity);

    T update(T entity);

    ArrayList<T> getList();

    Optional<T> getById(Long id);
}
