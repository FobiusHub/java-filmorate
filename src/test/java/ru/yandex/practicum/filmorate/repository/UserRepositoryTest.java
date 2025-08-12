package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;
import ru.yandex.practicum.filmorate.repository.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserRowMapper.class})
public class UserRepositoryTest {
    private final UserStorage userStorage;
    private final JdbcTemplate jdbc;
    private User user;
    private User user2;
    private List<User> list;

    private void initializeUsers() {
        user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("login123");
        user.setName("someName");
        user.setBirthday(LocalDate.of(2000, 10, 12));
        user = userStorage.add(user);

        user2 = new User();
        user2.setEmail("123@gmail.ru");
        user2.setLogin("456login123");
        user2.setName("someName2");
        user2.setBirthday(LocalDate.of(2002, 10, 12));
        user2.addFriend(user.getId());
        userStorage.add(user2);

        list = new ArrayList<>();
        list.add(user);
        list.add(user2);
    }

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM friends");
        jdbc.update("DELETE FROM users");
        initializeUsers();
    }

    @Test
    public void shouldCorrectlyAddUserAndReturnItFromDb() {
        User user3 = userStorage.get(user.getId());
        User user4 = userStorage.get(user2.getId());

        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(user3);

        assertThat(user2)
                .usingRecursiveComparison()
                .isEqualTo(user4);
    }

    @Test
    public void shouldCorrectlyUpdateUserInDb() {
        user.setEmail("123@123.ru");
        userStorage.update(user);

        User user2 = userStorage.get(user.getId());

        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(user2);
    }

    @Test
    public void shouldCorrectlyRemoveUserFromDb() {
        List<User> users = userStorage.getAll();

        assertThat(list)
                .usingRecursiveComparison()
                .isEqualTo(users);


        userStorage.delete(user2.getId());
        list.remove(1);
        users = userStorage.getAll();

        assertThat(list)
                .usingRecursiveComparison()
                .isEqualTo(users);
    }

    @Test
    public void shouldBeTrueIfUserExist() {
        assertTrue(userStorage.exists(user.getId()));
    }

    @Test
    public void shouldAddFriend() {
        user.addFriend(user2.getId());
        userStorage.addFriend(1, 2);

        User user3 = userStorage.get(1);
        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(user3);
    }

    @Test
    public void shouldRemoveFriend() {
        long userId = user.getId();
        long user2Id = user2.getId();
        user2.removeFriend(userId);
        userStorage.removeFriend(user2Id, userId);

        User user3 = userStorage.get(userId);
        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(user3);
    }
}
