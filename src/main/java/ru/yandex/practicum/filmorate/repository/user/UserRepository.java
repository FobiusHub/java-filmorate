package ru.yandex.practicum.filmorate.repository.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository("userDb")
public class UserRepository extends BaseRepository<User> implements UserStorage {
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_FRIENDS_QUERY = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIENDS_QUERY = "DELETE FROM friends WHERE user_id = ?";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String IS_EXIST_QUERY = "SELECT COUNT(*) FROM users WHERE user_id = ?";
    private static final String GET_FRIENDS_QUERY = "SELECT friend_id FROM friends WHERE user_id = ?";
    private static final String COMMON_FRIENDS_QUERY = "SELECT * FROM users u, friends f, friends o " +
            "WHERE u.user_id = f.friend_id AND u.user_id = o.friend_id AND f.user_id = ? AND o.user_id = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User add(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        addMany(INSERT_FRIENDS_QUERY, id, user.getFriends());

        return user;
    }

    @Override
    public void update(User user) {
        long id = user.getId();
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                id
        );
        delete(REMOVE_FRIENDS_QUERY, id);
        addMany(INSERT_FRIENDS_QUERY, id, user.getFriends());
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public User get(long id) {
        Optional<User> optionalUser = findOne(FIND_BY_ID_QUERY, id);
        if (optionalUser.isEmpty()) {
            log.warn("При запросе данных пользователя возникла ошибка: Пользователь не найден");
            throw new NotFoundException("Пользователь " + id + " не найден");
        }
        User user = optionalUser.get();
        setFriends(user);

        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = findMany(FIND_ALL_QUERY);
        for (User user : users) {
            setFriends(user);
        }
        return users;
    }

    @Override
    public boolean exists(long id) {
        long count = jdbc.queryForObject(IS_EXIST_QUERY, Long.class, id);
        return count > 0;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        jdbc.update(INSERT_FRIENDS_QUERY, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbc.update(REMOVE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public List<Long> getFriends(long userId) {
        return jdbc.queryForList(GET_FRIENDS_QUERY, Long.class, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        return findMany(COMMON_FRIENDS_QUERY, userId, otherId);
    }

    private void setFriends(User user) {
        List<Long> friends = jdbc.queryForList(GET_FRIENDS_QUERY, Long.class, user.getId());
        for (Long friendId : friends) {
            user.addFriend(friendId);
        }
    }
}
