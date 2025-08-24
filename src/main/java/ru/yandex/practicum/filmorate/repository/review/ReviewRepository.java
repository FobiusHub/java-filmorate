package ru.yandex.practicum.filmorate.repository.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component("reviewDb")
public class ReviewRepository extends BaseRepository<Review> implements ReviewStorage {
    private static final String INSERT_QUERY = "INSERT INTO reviews(content, positive, user_id, film_id, useful)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, positive = ?, user_id = ?, " +
            "film_id = ?, useful = ? WHERE review_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_MANY_QUERY = "SELECT * FROM reviews ORDER BY useful DESC " +
            "LIMIT ?";
    private static final String FIND_MANY_BY_FILM_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ? " +
            "ORDER BY useful DESC LIMIT ?";
    private static final String IS_EXIST_QUERY = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review add(Review review) {
        long id = insert(
                INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        );
        review.setReviewId(id);

        return review;
    }

    @Override
    public void update(Review review) {
        long id = review.getReviewId();
        update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                id
        );
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public Review get(long id) {
        Optional<Review> optionalReview = findOne(FIND_BY_ID_QUERY, id);
        if (optionalReview.isEmpty()) {
            log.warn("При запросе данных отзыва возникла ошибка: Отзыв не найден");
            throw new NotFoundException("Отзыв " + id + " не найден");
        }
        Review review = optionalReview.get();

        return review;
    }

    @Override
    public List<Review> getMany(long filmId, long count) {
        List<Review> reviews;
        if (filmId == -1) {
            reviews = findMany(FIND_MANY_QUERY, count);
        } else {
            reviews = findMany(FIND_MANY_BY_FILM_ID_QUERY, filmId, count);
        }

        return reviews;
    }

    @Override
    public boolean exists(long id) {
        long count = jdbc.queryForObject(IS_EXIST_QUERY, Long.class, id);
        return count > 0;
    }
}
