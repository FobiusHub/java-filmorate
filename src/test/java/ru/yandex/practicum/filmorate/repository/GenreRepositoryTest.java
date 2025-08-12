package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreStorage;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreRepository.class, GenreRowMapper.class})
public class GenreRepositoryTest {
    private final GenreStorage genreStorage;

    @Test
    public void testFindGenreById() {
        Genre g = new Genre();
        g.setId(1);
        g.setName("Комедия");

        Genre genreDb = genreStorage.get(1);

        assertThat(g)
                .usingRecursiveComparison()
                .isEqualTo(genreDb);
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = new ArrayList<>();

        Genre g1 = new Genre();
        g1.setId(1);
        g1.setName("Комедия");
        genres.add(g1);

        Genre g2 = new Genre();
        g2.setId(2);
        g2.setName("Драма");
        genres.add(g2);

        Genre g3 = new Genre();
        g3.setId(3);
        g3.setName("Мультфильм");
        genres.add(g3);

        Genre g4 = new Genre();
        g4.setId(4);
        g4.setName("Триллер");
        genres.add(g4);

        Genre g5 = new Genre();
        g5.setId(5);
        g5.setName("Документальный");
        genres.add(g5);

        Genre g6 = new Genre();
        g6.setId(6);
        g6.setName("Боевик");
        genres.add(g6);

        List<Genre> genresDb = genreStorage.getAll();

        assertThat(genres)
                .usingRecursiveComparison()
                .isEqualTo(genresDb);
    }
}

