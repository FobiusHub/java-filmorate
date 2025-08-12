package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreStorage;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class,
        MpaRepository.class, MpaRowMapper.class,
        GenreRepository.class, GenreRowMapper.class,
        UserRepository.class, UserRowMapper.class})
public class FilmRepositoryTest {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbc;
    private Film film;
    private Film film2;
    private User user;
    private User user2;
    private List<Film> list;

    private void initializeFilms() {
        user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login123");
        user.setName("someName");
        user.setBirthday(LocalDate.of(2000, 10, 12));
        user = userStorage.add(user);

        user2 = new User();
        user2.setEmail("123@gmail.ru");
        user2.setLogin("456login123");
        user2.setName("someName2");
        user2.setBirthday(LocalDate.of(2002, 10, 12));
        user2.addFriend(user.getId());
        userStorage.add(user2);

        film = new Film();
        film.setName("filmName");
        film.setDescription("filmDescription");
        film.setReleaseDate(LocalDate.of(2005, 10, 12));
        film.setDuration(120);
        film.setMpa(mpaStorage.get(1));
        film.addGenre(genreStorage.get(1));
        film.addGenre(genreStorage.get(2));
        filmStorage.add(film);

        film2 = new Film();
        film2.setName("filmName2");
        film2.setDescription("filmDescription2");
        film2.setReleaseDate(LocalDate.of(1950, 10, 12));
        film2.setDuration(180);
        film2.setMpa(mpaStorage.get(3));
        film2.addGenre(genreStorage.get(3));
        filmStorage.add(film2);

        list = new ArrayList<>();
        list.add(film);
        list.add(film2);
    }

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM films");
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM film_genres");
        initializeFilms();
    }

    @Test
    public void shouldCorrectlyAddFilm() {
        Film film3 = filmStorage.get(film.getId());
        Film film4 = filmStorage.get(film2.getId());

        assertThat(film)
                .usingRecursiveComparison()
                .isEqualTo(film3);

        assertThat(film2)
                .usingRecursiveComparison()
                .isEqualTo(film4);
    }

    @Test
    public void shouldCorrectlyUpdateFilm() {
        long id = film.getId();
        film = film2;
        film.setId(id);

        filmStorage.update(film);

        Film film3 = filmStorage.get(id);

        assertThat(film)
                .usingRecursiveComparison()
                .isEqualTo(film3);
    }

    @Test
    public void shouldCorrectlyRemoveFilm() {
        filmStorage.delete(film.getId());
        assertFalse(filmStorage.exists(film.getId()));
    }

    @Test
    public void shouldReturnAllFilms() {
        List<Film> listDb = filmStorage.getAll();
        assertThat(list)
                .usingRecursiveComparison()
                .isEqualTo(listDb);
    }

    @Test
    public void likeShouldAddToFilm() {
        film2.like(user.getId());
        filmStorage.like(film2.getId(), user.getId());

        Film filmTest = filmStorage.get(film2.getId());

        assertThat(film2)
                .usingRecursiveComparison()
                .isEqualTo(filmTest);
    }

    @Test
    public void likeShouldBeRemovedFromFilm() {
        film.removeLike(user.getId());
        filmStorage.removeLike(film.getId(), user.getId());

        Film filmTest = filmStorage.get(film.getId());

        assertThat(film)
                .usingRecursiveComparison()
                .isEqualTo(filmTest);
    }

    @Test
    public void getTopFilmsShouldReturn1MostLikelyFilm() {
        List<Film> top = new ArrayList<>();
        top.add(film);

        List<Film> topDb = filmStorage.getTopFilms(1);

        assertThat(top)
                .usingRecursiveComparison()
                .isEqualTo(topDb);
    }
}
