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
        try {
            validateFilmData(film);
        } catch (ValidationException e) {
            log.warn("При добавлении фильма возникла ошибка: {}", e.getMessage(), e);
            throw e;
        }
        film.setId(id);
        log.debug("Фильму {} присвоен id {}", film.getName(), id);
        id++;
        films.put(film.getId(), film);
        return film;
    }

    //обновление фильма
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilmData) {
        try {
            validateFilmData(newFilmData);
            return updateFilmData(newFilmData);
        } catch (ValidationException | NotFoundException e) {
            log.warn("При обновлении фильма возникла ошибка: {}", e.getMessage(), e);
            throw e;
        }
    }

    //получение всех фильмов
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    private void validateFilmData(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate != null && releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    private Film updateFilmData(Film newFilmData) {
        Long newFilmDataId = newFilmData.getId();
        if (newFilmDataId == null) {
            throw new ValidationException("Необходимо указать ID");
        }
        if (!films.containsKey(newFilmDataId)) {
            throw new NotFoundException("Фильм не найден");
        }

        Film film = films.get(newFilmDataId);

        String oldName = film.getName();
        String newName = newFilmData.getName();
        film.setName(newName);
        log.debug("Фильм id: {} - название \"{}\" изменено на \"{}\"",
                newFilmDataId, oldName, newName);

        String newFilmDescription = newFilmData.getDescription();
        if (newFilmDescription != null && !newFilmDescription.isBlank()) {
            String oldDescription = film.getDescription();
            film.setDescription(newFilmDescription);
            log.debug("Фильм id: {} - описание \"{}\" изменено на \"{}\"",
                    newFilmDataId, oldDescription, newFilmDescription);
        }

        LocalDate releaseDate = newFilmData.getReleaseDate();
        if (releaseDate != null) {
            String oldReleaseDate = film.getReleaseDate().toString();
            String newReleaseDate = releaseDate.toString();
            film.setReleaseDate(releaseDate);
            log.debug("Фильм id: {} - дата релиза \"{}\" изменена на \"{}\"",
                    newFilmDataId, oldReleaseDate, newReleaseDate);
        }

        Long duration = newFilmData.getDuration();
        if (duration != null) {
            Long oldDuration = film.getDuration();
            film.setDuration(duration);
            log.debug("Фильм id: {} - продолжительность \"{}\" изменена на \"{}\"",
                    newFilmDataId, oldDuration, duration);
        }

        return film;
    }
}
