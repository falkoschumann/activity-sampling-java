package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;

class ActivitiesServiceTests {
  private EventStore eventStore;
  private ActivitiesService sut;

  @BeforeEach
  void init() {
    eventStore = mock(EventStore.class);
    var clock = Clock.fixed(Instant.parse("2022-11-16T11:26:00Z"), ZoneId.systemDefault());
    sut = new ActivitiesServiceImpl(eventStore, clock);
  }

  @Test
  void logActivity_RecordsActivityLogged() {
    sut.logActivity("Lorem ipsum");

    verify(eventStore)
        .record(new ActivityLoggedEvent(Instant.parse("2022-11-16T11:26:00Z"), "Lorem ipsum"));
  }

  @Test
  void selectRecentActivities_ReturnsActivitiesGroupByDay() {
    when(eventStore.replay())
        .thenReturn(
            Stream.of(
                new ActivityLoggedEvent(Instant.parse("2022-11-15T12:32:00Z"), "A1"),
                new ActivityLoggedEvent(Instant.parse("2022-11-16T11:06:00Z"), "A2"),
                new ActivityLoggedEvent(Instant.parse("2022-11-16T11:26:00Z"), "A3")));

    var activities = sut.selectRecentActivities();

    assertEquals(
        new RecentActivities(
            List.of(
                new WorkingDay(
                    LocalDate.of(2022, 11, 16),
                    List.of(
                        new Activity(LocalDateTime.of(2022, 11, 16, 12, 26), "A3"),
                        new Activity(LocalDateTime.of(2022, 11, 16, 12, 6), "A2"))),
                new WorkingDay(
                    LocalDate.of(2022, 11, 15),
                    List.of(new Activity(LocalDateTime.of(2022, 11, 15, 13, 32), "A1"))))),
        activities);
  }

  @Test
  void selectRecentActivities_ReturnsOnly31Days() {
    when(eventStore.replay())
        .thenReturn(
            Stream.of(
                new ActivityLoggedEvent(Instant.parse("2022-10-16T12:32:00Z"), "A1"),
                new ActivityLoggedEvent(Instant.parse("2022-10-17T11:06:00Z"), "A2"),
                new ActivityLoggedEvent(Instant.parse("2022-11-16T11:26:00Z"), "A3")));

    var activities = sut.selectRecentActivities();

    assertEquals(
        new RecentActivities(
            List.of(
                new WorkingDay(
                    LocalDate.of(2022, 11, 16),
                    List.of(new Activity(LocalDateTime.of(2022, 11, 16, 12, 26), "A3"))),
                new WorkingDay(
                    LocalDate.of(2022, 10, 17),
                    List.of(new Activity(LocalDateTime.of(2022, 10, 17, 13, 6), "A2"))))),
        activities);
  }
}
