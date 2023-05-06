package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getGenres() {
        log.info("getGenres");
        return genreStorage.getList();
    }

    public Genre getGenreById(Long id) {
        log.info("getGenreById");
        return genreStorage.getById(id).orElseThrow(() -> {
            log.warn("genre with id={} not exist", id);
            throw new EntityNotExistException(String.format("Жанр с id=%d не существует.", id));
        });
    }
}
