package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MpaRepository extends BaseRepository<Mpa> implements MpaStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa ORDER BY mpa_id";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Mpa get(long id) {
        Optional<Mpa> mpa = findOne(FIND_BY_ID_QUERY, id);
        if (mpa.isEmpty()) {
            log.warn("При запросе рейтинга возникла ошибка: Рейтинг не найден");
            throw new NotFoundException("Рейтинг " + id + " не найден");
        }
        return mpa.get();
    }

    @Override
    public List<Mpa> getAll() {
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
