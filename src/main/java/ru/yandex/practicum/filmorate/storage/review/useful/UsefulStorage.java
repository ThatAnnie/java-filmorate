package ru.yandex.practicum.filmorate.storage.review.useful;

public interface UsefulStorage {
    void addLike(long id, long userId);

    void deleteLike(Long id, Long userId);

    void addDislike(long id, long userId);

    void deleteDislike(Long id, Long userId);
}
