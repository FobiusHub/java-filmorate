package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.event.EventStorage;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.review.ReviewStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public Review create(Review review) {
        long userId = review.getUserId();
        if (!userStorage.exists(userId)) {
            log.warn("При создании отзыва возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        long filmId = review.getFilmId();
        if (!filmStorage.exists(filmId)) {
            log.warn("При создании отзыва возникла ошибка: Фильм не найден");
            throw new NotFoundException("Фильм " + filmId + " не найден");
        }
        reviewStorage.add(review);
        eventStorage.add(new Event(userId, EventType.REVIEW, Operation.ADD, review.getReviewId()));
        return review;
    }

    public void update(Review newReviewData) {
        Long newReviewDataId = newReviewData.getReviewId();
        long userId = newReviewData.getUserId();
        if (newReviewDataId == null) {
            log.warn("При обновлении данных отзыва возникла ошибка: Необходимо указать ID");
            throw new ValidationException("Необходимо указать ID");
        }
        if (!reviewStorage.exists(newReviewDataId)) {
            log.warn("При обновлении данных отзыва возникла ошибка: Отзыв не найден");
            throw new NotFoundException("Отзыв " + newReviewDataId + " не найден");
        }
        checkUserExist(userId);
        reviewStorage.update(newReviewData);
        eventStorage.add(new Event(userId, EventType.REVIEW, Operation.UPDATE, newReviewDataId));
    }

    public Review deleteReview(long id) {
        Review review = reviewStorage.get(id);
        reviewStorage.delete(id);
        eventStorage.add(new Event(review.getUserId(), EventType.REVIEW, Operation.REMOVE, id));
        return review;
    }

    public Review getReview(long id) {
        return reviewStorage.get(id);
    }

    public List<Review> getReviews(long filmId, long count) {
        if (filmId != -1 && !filmStorage.exists(filmId)) {
            log.warn("При запросе данных отзыва возникла ошибка: Отзыв не найден");
            throw new NotFoundException("Отзыв " + filmId + " не найден");
        }
        return reviewStorage.getMany(filmId, count);
    }

    public void like(long reviewId, long userId) {
        checkUserExist(userId);
        Review review = reviewStorage.get(reviewId);
        review.like();
        reviewStorage.update(review);
    }

    public void dislike(long reviewId, long userId) {
        checkUserExist(userId);
        Review review = reviewStorage.get(reviewId);
        review.dislike();
        reviewStorage.update(review);
    }

    public void deleteLike(long reviewId, long userId) {
        checkUserExist(userId);
        Review review = reviewStorage.get(reviewId);
        review.removeLike();
        reviewStorage.update(review);
    }

    public void deleteDislike(long reviewId, long userId) {
        checkUserExist(userId);
        Review review = reviewStorage.get(reviewId);
        review.removeDislike();
        reviewStorage.update(review);
    }

    private void checkUserExist(long userId) {
        if (!userStorage.exists(userId)) {
            log.warn("При обновлении данных отзыва возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
    }
}
