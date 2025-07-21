package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        user.setId(id);
        log.debug("Пользователю {} присвоен id {}", user.getLogin(), id);
        id++;
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void update(User newUserData) {
        users.put(newUserData.getId(), newUserData);
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public User get(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean exists(long id) {
        return users.containsKey(id);
    }
}
