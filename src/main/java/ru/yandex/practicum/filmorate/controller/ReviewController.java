package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.ReviewGradesService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewGradesService reviewGradesService;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewGradesService reviewGradesService) {
        this.reviewService = reviewService;
        this.reviewGradesService = reviewGradesService;
    }

    @PostMapping
    public Review saveReview(@RequestBody @Valid Review review) {
        return reviewService.saveReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping()
    public List<Review> getReviewsWithParam(@RequestParam(required = false) Long filmId,
                                            @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getReviewsWithParam(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewGradesService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewGradesService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewGradesService.deleteLikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        reviewGradesService.deleteDislikeFromReview(id, userId);
    }
}