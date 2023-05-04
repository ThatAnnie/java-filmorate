package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface LikeStorage {
    void addLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    Collection<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    Collection<Long> getUsersLikesByFilm(Long userId);

    Collection<Film> getSortedFilmByLikesDirector(Long dirId);

    List<Film> getCommonFilms(Long userId, Long friendId);

}
