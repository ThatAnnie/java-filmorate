package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    public Review saveReview(Review review) {
        log.info("save review {} ", review);
        review = reviewStorage.save(review);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, Operation.ADD, review.getReviewId());
        return review;
    }

    public Review updateReview(Review review) {
        log.info("update review {} ", review);
        review = reviewStorage.update(review);
        eventService.createEvent(review.getUserId(), EventType.REVIEW, Operation.UPDATE, review.getReviewId());
        return review;
    }

    public Review getReviewById(Long id) {
        log.info("get review by id {} ", id);
        return reviewStorage.getById(id).get();
    }

    public void deleteReview(Long id) {
        log.info("delete review with id {} ", id);
        Review review = reviewStorage.getById(id).get();
        eventService.createEvent(review.getUserId(), EventType.REVIEW, Operation.REMOVE, review.getReviewId());
        reviewStorage.delete(id);
    }

    public List<Review> getReviewsWithParam(Long filmId, Integer count) {
        log.info("get reviews with params");
        return reviewStorage.getListWithParam(filmId, count);
    }
}
