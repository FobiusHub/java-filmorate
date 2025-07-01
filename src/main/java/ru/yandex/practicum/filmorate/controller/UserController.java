package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
        try {
            validateUserData(user);
        } catch (ValidationException e) {
            log.warn("При добавлении пользователя возникла ошибка: {}", e.getMessage(), e);
            throw e;
        }
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
        try {
            validateUserData(newUserData);
            return updateUserData(newUserData);
        } catch (ValidationException | NotFoundException e) {
            log.warn("При обновлении данных пользователя возникла ошибка: {}", e.getMessage(), e);
            throw e;
        }
    }

    //получение списка всех пользователей
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    private void validateUserData(User user) {
        String login = user.getLogin();
        if (login.contains(" ") || login.isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    private User updateUserData(User newUserData) {
        Long newUserDataId = newUserData.getId();
        if (newUserDataId == null) {
            throw new ValidationException("Необходимо указать ID");
        }
        if (!users.containsKey(newUserDataId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        User user = users.get(newUserDataId);

        String oldEmail = user.getEmail();
        String newEmail = newUserData.getEmail();
        user.setEmail(newEmail);
        log.debug("Пользователь id: {} - email \"{}\" изменен на \"{}\"",
                newUserDataId, oldEmail, newEmail);

        String newLogin = newUserData.getLogin();
        if (!newLogin.isBlank()) {
            String oldLogin = user.getLogin();
            user.setLogin(newLogin);
            log.debug("Пользователь id: {} - логин \"{}\" изменен на \"{}\"",
                    newUserDataId, oldLogin, newLogin);
        }

        String newName = newUserData.getName();
        if (newName != null && !newName.isBlank()) {
            String oldName = user.getName();
            user.setName(newName);
            log.debug("Пользователь id: {} - имя \"{}\" изменено на \"{}\"",
                    newUserDataId, oldName, newName);
        }

        LocalDate newBirthday = newUserData.getBirthday();
        if (newBirthday != null) {
            LocalDate oldBirthday = user.getBirthday();
            user.setBirthday(newBirthday);
            log.debug("Пользователь id: {} - дата рождения \"{}\" изменена на \"{}\"",
                    newUserDataId, oldBirthday, newBirthday);
        }

        return user;
    }
}
