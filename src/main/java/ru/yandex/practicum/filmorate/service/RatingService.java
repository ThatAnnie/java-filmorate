package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class RatingService {
    private final RatingStorage ratingStorage;

    public List<Rating> getRatings() {
        log.info("getRatings");
        return ratingStorage.getList();
    }

    public Rating getRatingById(Long id) {
        log.info("getRatingById");
        return ratingStorage.getById(id).orElseThrow(() -> {
            log.warn("mpa with id={} not exist", id);
            throw new EntityNotExistException(String.format("MPA с id=%d не существует.", id));
        });
    }
}
