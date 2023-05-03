package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate"})
@SqlGroup({
        @Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EventTest {
    @Autowired
    private final UserDbStorage userDbStorage;
    @Autowired
    private final EventStorage eventStorage;

    @Test
    void testCreateEventGetEvents() {
        User user1 = new User();
        user1.setName("UserName1");
        user1.setLogin("login");
        user1.setEmail("test@test.ru");
        user1.setBirthday(LocalDate.of(1990, 03, 9));
        User userDB1 = userDbStorage.save(user1);

        Event event1 = new Event();
        event1.setUserId(userDB1.getId());
        event1.setEventType(EventType.FRIEND);
        event1.setOperation(Operation.ADD);
        event1.setEntityId(123L);
        event1.setTimestamp(Instant.now().toEpochMilli());
        Event event2 = new Event();
        event2.setUserId(userDB1.getId());
        event2.setEventType(EventType.LIKE);
        event2.setOperation(Operation.ADD);
        event2.setEntityId(11L);
        event2.setTimestamp(Instant.now().toEpochMilli());

        List<Event> events = new ArrayList<>(eventStorage.getUserEvents(userDB1.getId()));
        assertThat(events.size()).isEqualTo(0);
        eventStorage.createEvent(event1);
        eventStorage.createEvent(event2);
        events = new ArrayList<>(eventStorage.getUserEvents(userDB1.getId()));
        assertThat(events.size()).isEqualTo(2);
        assertThat(events.get(0).getEventId()).isEqualTo(1);
        assertThat(events.get(0).getUserId()).isEqualTo(userDB1.getId());
        assertThat(events.get(0).getEventType()).isEqualTo(EventType.FRIEND);
        assertThat(events.get(0).getOperation()).isEqualTo(Operation.ADD);

        assertThat(events.get(1).getEventId()).isEqualTo(2);
        assertThat(events.get(1).getUserId()).isEqualTo(userDB1.getId());
        assertThat(events.get(1).getEventType()).isEqualTo(EventType.LIKE);
        assertThat(events.get(1).getOperation()).isEqualTo(Operation.ADD);
    }
}
