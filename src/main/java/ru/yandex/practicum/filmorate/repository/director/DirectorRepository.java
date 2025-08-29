package ru.yandex.practicum.filmorate.repository.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.BaseRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class DirectorRepository extends BaseRepository<Director> implements DirectorStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors ORDER BY director_id";
    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = ?";
    private static final String IS_EXIST_QUERY = "SELECT COUNT(*) FROM directors WHERE director_id = ?";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Director get(long id) {
        Optional<Director> director = findOne(FIND_BY_ID_QUERY, id);
        if (director.isEmpty()) {
            log.warn("При запросе режиссера возникла ошибка: Режиссер не найден");
            throw new NotFoundException("Режиссер " + id + " не найден");
        }
        return director.get();
    }

    @Override
    public List<Director> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Director add(Director director) {
        long id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        long id = director.getId();
        update(
                UPDATE_QUERY,
                director.getName(),
                id
        );
        return director;
    }

    @Override
    public void delete(long id) {
        delete(
                DELETE_QUERY,
                id
        );
    }

    @Override
    public boolean exists(long id) {
        long count = jdbc.queryForObject(IS_EXIST_QUERY, Long.class, id);
        return count > 0;
    }
}
