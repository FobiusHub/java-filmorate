package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilmData) {
        filmService.update(newFilmData);
        return newFilmData;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAll();
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable long id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        filmService.like(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") long count) {
        return filmService.getTopFilms(count);
    }

    @DeleteMapping("{id}")
    public Film deleteFilm(@PathVariable long id) {
        return filmService.deleteFilm(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@PathVariable long directorId, @RequestParam String sortBy) {
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getFilmsSearch(@RequestParam String query, @RequestParam String by) {
        return filmService.getFilmsSearch(query, by);
    }
    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId,
                                     @RequestParam long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
