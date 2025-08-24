package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.mappers.*;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewStorage;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({ReviewRepository.class, ReviewRowMapper.class})
public class ReviewRepositoryTest {
    private final ReviewStorage reviewStorage;
    private final JdbcTemplate jdbc;

    private Review review;

    private void initialize() {
        review = new Review();
        review.setContent("Фильм ужасен");
        review.setIsPositive(false);
        review.setUserId(1L);
        review.setFilmId(1L);
        reviewStorage.add(review);
    }

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM reviews");
        initialize();
    }

    @Test
    public void shouldCorrectlyAddReviewAndReturnItFromDb() {
        Review review2 = reviewStorage.get(review.getReviewId());

        assertThat(review)
                .usingRecursiveComparison()
                .isEqualTo(review2);
    }

    @Test
    public void shouldCorrectlyUpdateReviewInDb() {
        review.setContent("Хуже не бывает");
        reviewStorage.update(review);

        Review review2 = reviewStorage.get(review.getReviewId());

        assertThat(review)
                .usingRecursiveComparison()
                .isEqualTo(review2);
    }

    @Test
    public void shouldCorrectlyRemoveReviewFromDb() {
        reviewStorage.delete(review.getReviewId());

        assertThrows(NotFoundException.class, () -> {
            reviewStorage.get(review.getReviewId());
        });
    }

    @Test
    public void shouldBeTrueIfReviewExist() {
        assertTrue(reviewStorage.exists(review.getReviewId()));
    }

    @Test
    public void shouldReturnReviewList() {
        Review review2 = new Review();
        review2.setContent("Фильм прекрасен");
        review2.setIsPositive(true);
        review2.setUserId(1L);
        review2.setFilmId(2L);
        reviewStorage.add(review2);

        List<Review> allReviewList = new ArrayList<>();
        allReviewList.add(review);
        allReviewList.add(review2);

        List<Review> filmTwoReviewList = new ArrayList<>();
        filmTwoReviewList.add(review2);

        assertThat(allReviewList)
                .usingRecursiveComparison()
                .isEqualTo(reviewStorage.getMany(-1, 2));

        assertThat(filmTwoReviewList)
                .usingRecursiveComparison()
                .isEqualTo(reviewStorage.getMany(2, 2));
    }
}
