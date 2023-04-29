package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.ReviewUsefulService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    ReviewService reviewService;
    ReviewUsefulService usefulService;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewUsefulService usefulService) {
        this.reviewService = reviewService;
        this.usefulService = usefulService;
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
    public List<Review> getReviewsWithParam(@RequestParam(defaultValue = "0") Long filmId,
                                            @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getReviewsWithParam(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        usefulService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long id, @PathVariable Long userId) {
        usefulService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        usefulService.deleteLikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeFromReview(@PathVariable Long id, @PathVariable Long userId) {
        usefulService.deleteDislikeFromReview(id, userId);
    }
}