package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film create(Film film) {
        validateFilmData(film);
        return filmStorage.add(film);
    }

    public void update(Film newFilmData) {
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
        if (!filmStorage.exists(id)) {
            log.warn("Фильм не найден");
            throw new NotFoundException("Фильм " + id + " не найден");
        }
        return filmStorage.get(id);
    }

    public void like(long filmId, long userId) {
        userService.validateUserExists(userId);
        if (!filmStorage.exists(filmId)) {
            log.warn("Не удалось поставить лайк: Фильм не найден");
            throw new NotFoundException("Фильм " + filmId + " не найден");
        }
        filmStorage.get(filmId).like(userId);
    }

    public void removeLike(long filmId, long userId) {
        userService.validateUserExists(userId);
        if (!filmStorage.exists(filmId)) {
            log.warn("Не удалось удалить лайк: Фильм не найден");
            throw new NotFoundException("Фильм " + filmId + " не найден");
        }
        filmStorage.get(filmId).removeLike(userId);
    }

    public List<Film> getTopFilms(long size) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparing(Film::getLikesCount).reversed())
                .limit(size)
                .toList();
    }

    private void validateFilmData(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("При добавлении/обновлении фильма возникла ошибка: Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }
}
