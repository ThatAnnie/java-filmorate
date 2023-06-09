package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    public List<Director> getDirectors() {
        log.info("getDirectors");
        return directorStorage.getList();
    }

    public Director saveDirector(Director director) {
        log.info("saveDirectors");
        return directorStorage.save(director);
    }

    public Director updateDirector(Director director) {
        log.info("updateDirectors");
        try {
            return directorStorage.update(director);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotExistException(e.getMessage());
        }
    }

    public Director getDirectorById(Long id) {
        log.info("getDirectorById with id={}", id);
        return directorStorage.getById(id).orElseThrow(() -> {
            log.warn("director with id={} not exist", id);
            throw new EntityNotExistException(String.format("Director с id=%d не существует.", id));
        });
    }

    public void deleteDirectorById(Long id) {
        log.info("getDirectorById with id={}", id);
        directorStorage.getById(id).orElseThrow(() -> {
            log.warn("director with id={} not exist", id);
            throw new EntityNotExistException(String.format("Director с id=%d не существует.", id));
        });
        directorStorage.delete(id);
    }
}
