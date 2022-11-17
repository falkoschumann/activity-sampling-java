package de.muspellheim.activitysampling.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;

class ActivitiesServiceTests {
  private EventStore eventStore;
  private MutableClock clock;
  private ActivitiesService sut;

  @BeforeEach
  void init() {
    eventStore = mock(EventStore.class);
    clock = new MutableClock();
    sut = new ActivitiesServiceImpl(eventStore, clock);
  }

  @Test
  void logActivity_RecordsActivityLogged() {
    clock.setTimestamp(Instant.parse("2022-11-16T11:26:00Z"));
    sut.logActivity("Lorem ipsum");

    verify(eventStore)
        .record(
            new ActivityLoggedEvent(
                Instant.parse("2022-11-16T11:26:00Z"), Duration.ofMinutes(20), "Lorem ipsum"));
  }

  @Test
  void selectRecentActivities_MonthWith30Days() {
    clock.setTimestamp(Instant.parse("2022-09-30T10:00:00Z"));
    when(eventStore.replay())
        .thenReturn(
            Stream.of(
                // Last day of last month
                new ActivityLoggedEvent(
                    Instant.parse("2022-08-31T15:00:00Z"), Duration.ofMinutes(20), "A1"),
                // First day of this month
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-01T14:00:00Z"), Duration.ofMinutes(20), "A2"),
                // This Month
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-05T13:00:00Z"), Duration.ofMinutes(20), "A3"),
                // This Week
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-26T12:00:00Z"), Duration.ofMinutes(20), "A4"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-28T11:00:00Z"), Duration.ofMinutes(20), "A5"),
                // Yesterday
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-29T09:00:00Z"), Duration.ofMinutes(20), "A6"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-29T10:00:00Z"), Duration.ofMinutes(20), "A7"),
                // Today
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-30T07:00:00Z"), Duration.ofMinutes(20), "A8"),
                new ActivityLoggedEvent(
                    Instant.parse("2022-09-30T08:00:00Z"), Duration.ofMinutes(20), "A9")));

    var activities = sut.selectRecentActivities();

    assertEquals(
        new RecentActivities(
            List.of(
                new WorkingDay(
                    LocalDate.of(2022, 9, 30),
                    List.of(
                        new Activity(LocalDateTime.of(2022, 9, 30, 10, 0), "A9"),
                        new Activity(LocalDateTime.of(2022, 9, 30, 9, 0), "A8"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 29),
                    List.of(
                        new Activity(LocalDateTime.of(2022, 9, 29, 12, 0), "A7"),
                        new Activity(LocalDateTime.of(2022, 9, 29, 11, 0), "A6"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 28),
                    List.of(new Activity(LocalDateTime.of(2022, 9, 28, 13, 0), "A5"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 26),
                    List.of(new Activity(LocalDateTime.of(2022, 9, 26, 14, 0), "A4"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 5),
                    List.of(new Activity(LocalDateTime.of(2022, 9, 5, 15, 0), "A3"))),
                new WorkingDay(
                    LocalDate.of(2022, 9, 1),
                    List.of(new Activity(LocalDateTime.of(2022, 9, 1, 16, 0), "A2"))),
                new WorkingDay(
                    LocalDate.of(2022, 8, 31),
                    List.of(new Activity(LocalDateTime.of(2022, 8, 31, 17, 0), "A1")))),
            new TimeSummary(
                Duration.ofMinutes(40),
                Duration.ofMinutes(40),
                Duration.ofMinutes(120),
                Duration.ofMinutes(160))),
        activities);
  }
}
