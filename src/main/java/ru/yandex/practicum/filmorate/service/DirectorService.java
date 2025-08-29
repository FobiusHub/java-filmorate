package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director getDirector(long id) {
        return directorStorage.get(id);
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAll();
    }

    public Director addDirector(Director director) {
        return directorStorage.add(director);
    }

    public Director updateDirector(Director director) {
        Long newDirectorId = director.getId();
        if (newDirectorId == null) {
            log.warn("При обновлении режиссера возникла ошибка: Необходимо указать ID");
            throw new ValidationException("Необходимо указать ID");
        }
        if (!directorStorage.exists(newDirectorId)) {
            log.warn("При обновлении режиссера возникла ошибка: Режиссер не найден");
            throw new NotFoundException("Режиссер " + newDirectorId + " не найден");
        }
        return directorStorage.update(director);
    }

    public void deleteDirector(long id) {
        directorStorage.delete(id);
    }
}
