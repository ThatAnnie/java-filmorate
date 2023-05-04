package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createEvent(Event event) {
        String sqlQuery = "INSERT INTO events (event_timestamp,  user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
            stmt.setLong(1, event.getTimestamp());
            stmt.setLong(2, event.getUserId());
            stmt.setString(3, event.getEventType().getTitle());
            stmt.setString(4, event.getOperation().getTitle());
            stmt.setLong(5, event.getEntityId());
            return stmt;
        }, keyHolder);
    }

    @Override
    public Collection<Event> getUserEvents(Long id) {
        String sql = "SELECT * FROM events WHERE user_id = ? ORDER BY event_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), id);
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getLong("event_id"));
        event.setTimestamp(rs.getLong("event_timestamp"));
        event.setUserId(rs.getLong("user_id"));
        event.setEventType(EventType.valueOf(rs.getString("event_type")));
        event.setOperation(Operation.valueOf(rs.getString("operation")));
        event.setEntityId(rs.getLong("entity_id"));
        return event;
    }
}
