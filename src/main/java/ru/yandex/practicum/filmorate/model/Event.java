package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Event {
    private long eventId;
    private long timestamp;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;

    public Event(long userId, EventType eventType, Operation operation, long entityId) {
        timestamp = Instant.now().toEpochMilli();
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
