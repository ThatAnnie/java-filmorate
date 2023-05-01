package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.review.useful.ReviewGradesStorage;

@Service
@Slf4j
public class ReviewGradesService {

    ReviewGradesStorage reviewGradesStorage;

    @Autowired
    public ReviewGradesService(ReviewGradesStorage reviewGradesStorage) {
        this.reviewGradesStorage = reviewGradesStorage;
    }

    public void addLikeToReview(long id, long userId) {
        log.info("User {} add like to review {}", userId, id);
        reviewGradesStorage.addLike(id, userId);
    }

    public void deleteLikeFromReview(long id, long userId) {
        log.info("User {} delete like from review {}", userId, id);
        reviewGradesStorage.deleteLike(id, userId);
    }

    public void addDislikeToReview(long id, long userId) {
        log.info("User {} add dislike to review {}", userId, id);
        reviewGradesStorage.addDislike(id, userId);
    }

    public void deleteDislikeFromReview(long id, long userId) {
        log.info("User {} delete dislike from review {}", userId, id);
        reviewGradesStorage.deleteDislike(id, userId);
    }
}
