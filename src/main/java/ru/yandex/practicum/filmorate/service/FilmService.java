package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.film.FilmStorage;
import ru.yandex.practicum.filmorate.repository.genre.GenreStorage;
import ru.yandex.practicum.filmorate.repository.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public Film create(Film film) {
        validateFilmData(film);
        return filmStorage.add(film);
    }

    public void update(Film newFilmData) {
        if (newFilmData == null) {
            return;
        }
        Long newFilmDataId = newFilmData.getId();
        if (newFilmDataId == null) {
            log.warn("При обновлении фильма возникла ошибка: Необходимо указать ID");
            throw new ValidationException("Необходимо указать ID");
        }
        if (!filmStorage.exists(newFilmDataId)) {
            log.warn("При обновлении фильма возникла ошибка: Фильм не найден");
            throw new NotFoundException("Фильм " + newFilmDataId + " не найден");
        }
        validateFilmData(newFilmData);
        filmStorage.update(newFilmData);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getFilm(long id) {
        return filmStorage.get(id);
    }

    public void like(long filmId, long userId) {
        if (!userStorage.exists(userId)) {
            log.warn("Не удалось поставить лайк: Пользователь не найден");
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        if (!filmStorage.exists(filmId)) {
            log.warn("Не удалось поставить лайк: Фильм не найден");
            throw new NotFoundException("Фильм " + filmId + " не найден");
        }
        filmStorage.like(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        userStorage.exists(userId);
        if (!filmStorage.exists(filmId)) {
            log.warn("Не удалось удалить лайк: Фильм не найден");
            throw new NotFoundException("Фильм " + filmId + " не найден");
        }
        filmStorage.get(filmId).removeLike(userId);
    }

    public List<Film> getTopFilms(long size) {
        return filmStorage.getTopFilms(size);
    }

    public Film deleteFilm(long id) {
        Film film = filmStorage.get(id);
        filmStorage.delete(id);
        return film;
    }

    private void validateFilmData(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("При добавлении/обновлении фильма возникла ошибка: Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        mpaStorage.get(film.getMpa().getId());

        Set<Genre> filmGenres = film.getGenres();
        if (filmGenres != null && !filmGenres.isEmpty()) {
            for (Genre genre : filmGenres) {
                //здесь происходит проверка наличия жанра в базе данных
                genreStorage.get(genre.getId());
            }
        }
    }
}
