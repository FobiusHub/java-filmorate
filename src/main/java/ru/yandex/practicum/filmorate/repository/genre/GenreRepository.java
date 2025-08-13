package ru.yandex.practicum.filmorate.repository.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GenreRepository extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Genre get(long id) {
        Optional<Genre> genre = findOne(FIND_BY_ID_QUERY, id);
        if (genre.isEmpty()) {
            log.warn("При запросе жанра возникла ошибка: Жанр не найден");
            throw new NotFoundException("Жанр " + id + " не найден");
        }
        return genre.get();
    }

    @Override
    public List<Genre> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    protected boolean delete(String query, long id) {
        throw new InternalServerException("Операция недоступна");
    }

    @Override
    protected void update(String query, Object... params) {
        throw new InternalServerException("Операция недоступна");
    }

    @Override
    protected long insert(String query, Object... params) {
        throw new InternalServerException("Операция недоступна");
    }

    @Override
    protected <E> void addMany(String query, long id, List<E> list) {
        throw new InternalServerException("Операция недоступна");
    }
}
