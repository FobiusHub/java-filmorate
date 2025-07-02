package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    //создание пользователя
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validateUserData(user);

        user.setId(id);
        log.debug("Пользователю {} присвоен id {}", user.getLogin(), id);
        id++;
        String name = user.getName();
        if (name == null || name.isBlank()) {
            String login = user.getLogin();
            user.setName(login);
            log.debug("Имя пользователя не указано. В качестве имени присвоен логин {}.", login);
        }
        users.put(user.getId(), user);
        return user;
    }

    //обновление пользователя
    @PutMapping
    public User update(@Valid @RequestBody User newUserData) {
        validateUserData(newUserData);
        return updateUserData(newUserData);
    }

    //получение списка всех пользователей
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    private void validateUserData(User user) {
        String login = user.getLogin();
        if (login.contains(" ") || login.isBlank()) {
            log.warn("При добавлении пользователя возникла ошибка: Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    private User updateUserData(User newUserData) {
        Long newUserDataId = newUserData.getId();
        if (newUserDataId == null) {
            log.warn("При обновлении данных пользователя возникла ошибка: Необходимо указать ID");
            throw new ValidationException("Необходимо указать ID");
        }
        if (!users.containsKey(newUserDataId)) {
            log.warn("При обновлении данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        users.put(newUserData.getId(), newUserData);
        return newUserData;
    }
}
