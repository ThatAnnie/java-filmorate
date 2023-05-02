package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService {

    ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    public Review saveReview(Review review) {
        log.info("save review {} ", review);
        return reviewStorage.save(review);
    }

    public Review updateReview(Review review) {
        log.info("update review {} ", review);
        return reviewStorage.update(review);
    }

    public Review getReviewById(Long id) {
        log.info("get review by id {} ", id);
        return reviewStorage.getById(id).get();
    }

    public void deleteReview(Long id) {
        log.info("delete review with id {} ", id);
        reviewStorage.delete(id);
    }

    public List<Review> getReviewsWithParam(Long filmId, Integer count) {
        log.info("get reviews with params");
        return reviewStorage.getListWithParam(filmId, count);
    }
}
