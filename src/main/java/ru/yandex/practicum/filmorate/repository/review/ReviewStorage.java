package ru.yandex.practicum.filmorate.repository.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add(Review review);

    void update(Review review);

    void delete(long id);

    Review get(long id);

    List<Review> getMany(long filmId, long count);

    boolean exists(long id);
}
