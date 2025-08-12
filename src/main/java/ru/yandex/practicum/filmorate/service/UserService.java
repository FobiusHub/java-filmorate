package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDb") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        checkName(user);
        return userStorage.add(user);
    }

    public void update(User newUserData) {
        Long newUserDataId = newUserData.getId();
        if (newUserDataId == null) {
            log.warn("При обновлении данных пользователя возникла ошибка: Необходимо указать ID");
            throw new ValidationException("Необходимо указать ID");
        }
        if (!userStorage.exists(newUserDataId)) {
            log.warn("При обновлении данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + newUserDataId + " не найден");
        }
        checkName(newUserData);
        userStorage.update(newUserData);
    }

    public List<User> findAll() {
        return userStorage.getAll();
    }

    public User getUser(long id) {
        return userStorage.get(id);
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.get(userId);
        if (!userStorage.exists(friendId)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + friendId + " не найден");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.get(userId);
        if (!userStorage.exists(friendId)) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + friendId + " не найден");
        }
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(long id) {
        User user = userStorage.get(id);
        return user.getFriends().stream()
                .map(userStorage::get)
                .toList();
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    private void checkName(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            String login = user.getLogin();
            user.setName(login);
            log.debug("Имя пользователя не указано. В качестве имени присвоен логин {}.", login);
        }
    }
}
