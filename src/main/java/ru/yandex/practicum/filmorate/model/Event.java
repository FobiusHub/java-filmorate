package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Event {
    private long eventId;
    @NotNull(message = "Временная метка события обязательна")
    private long timestamp;
    @NotNull(message = "id пользователя должно быть указано")
    private Long userId;
    @NotNull(message = "Тип события должен быть указан")
    private EventType eventType;
    @NotNull(message = "Тип операции должен быть указан")
    private Operation operation;
    @NotNull(message = "id сущности должно быть указано")
    private Long entityId;

    public Event(long userId, EventType eventType, Operation operation, long entityId) {
        timestamp = Instant.now().toEpochMilli();
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
