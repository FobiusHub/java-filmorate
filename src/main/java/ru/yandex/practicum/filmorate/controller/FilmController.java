package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private long id = 1;
    private final Map<Long, Film> films = new HashMap<>();

    //добавление фильма
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateFilmData(film);
        film.setId(id);
        log.debug("Фильму {} присвоен id {}", film.getName(), id);
        id++;
        films.put(film.getId(), film);
        return film;
    }

    //обновление фильма
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilmData) {
        validateFilmData(newFilmData);
        return updateFilmData(newFilmData);
    }

    //получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    private void validateFilmData(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("При добавлении/обновлении фильма возникла ошибка: Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    private Film updateFilmData(Film newFilmData) {
        Long newFilmDataId = newFilmData.getId();
        if (newFilmDataId == null) {
            log.warn("При обновлении фильма возникла ошибка: Необходимо указать ID");
            throw new ValidationException("Необходимо указать ID");
        }
        if (!films.containsKey(newFilmDataId)) {
            log.warn("При обновлении фильма возникла ошибка: Фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }
        films.put(newFilmData.getId(), newFilmData);
        return newFilmData;
    }
}
