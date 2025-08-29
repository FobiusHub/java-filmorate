package ru.yandex.practicum.filmorate.repository.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.repository.BaseRepository;

import java.util.List;

@Slf4j
@Repository("eventDb")
public class EventRepository extends BaseRepository<Event> implements EventStorage {
    private static final String INSERT_QUERY = "INSERT INTO events(timestamp, user_id, event_type, operation, " +
            "entity_id) VALUES (?, ?, ?, ?, ?)";
    private static final String USER_EVENTS_QUERY = "SELECT * FROM events WHERE user_id = ?";

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void add(Event event) {
        insert(
                INSERT_QUERY,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId()
        );
    }

    @Override
    public List<Event> getFeed(long userId) {
        return findMany(USER_EVENTS_QUERY, userId);
    }
}
