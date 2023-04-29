package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.review.useful.UsefulStorage;

@Service
@Slf4j
public class ReviewUsefulService {

    UsefulStorage usefulStorage;

    @Autowired
    public ReviewUsefulService(UsefulStorage usefulStorage) {
        this.usefulStorage = usefulStorage;
    }

    public void addLikeToReview(long id, long userId) {
        log.info("User {} add like to review {}", userId, id);
        usefulStorage.addLike(id, userId);
    }

    public void deleteLikeFromReview(long id, long userId) {
        log.info("User {} delete like from review {}", userId, id);
        usefulStorage.deleteLike(id, userId);
    }

    public void addDislikeToReview(long id, long userId) {
        log.info("User {} add dislike to review {}", userId, id);
        usefulStorage.addDislike(id, userId);
    }

    public void deleteDislikeFromReview(long id, long userId) {
        log.info("User {} delete dislike from review {}", userId, id);
        usefulStorage.deleteDislike(id, userId);
    }
}
